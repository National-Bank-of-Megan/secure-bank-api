package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.*;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.dto.VerifyDeviceWithCodeRequest;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.DeviceRepository;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.PasswordUtil;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final DevicesService devicesService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final DeviceRepository deviceRepository;

    @Override
    public SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData, HttpServletRequest request) {
        if (accountRepository.findByAccountDetailsEmail(registerData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is already taken");
        }

        String deviceName = HttpRequestUtils.getDeviceNameFromRequest(request, devicesService);
        String rawPassword = registerData.getPassword();
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        List<Account> allAccounts = accountRepository.findAll();
        Set<String> existingAccountsNumbers = allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Set<String> existingClientIds = allAccounts.stream().map(Account::getClientId).collect(Collectors.toSet());
        Account accountToRegister = AccountMapper.map(registerData, existingAccountsNumbers, existingClientIds);
        PasswordUtil.addAccountHashes(accountToRegister, rawPassword, passwordEncoder);

        accountToRegister.addSubAccounts(Currency.values());
        accountToRegister.setAccountDetails(new AccountDetails(registerData.getFirstName(), registerData.getLastName(), registerData.getEmail(), "666 666 666"));

        accountToRegister.addDevice(new Device(registerData.getDeviceFingerprint(), deviceName, LocalDateTime.now(), registerData.getIp()));

        String secret = otpService.generateSecret();
        accountToRegister.setSecret(secret);

        String generatedClientId = accountRepository.save(accountToRegister).getClientId();

        otpService.getUriForImage(secret);
        String qrImageUri = otpService.getUriForImage(secret);
        return new SuccessfulRegistrationResponse(generatedClientId, qrImageUri);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber).orElse(null);
    }

    @Override
    public boolean verifyDevice(VerifyDeviceWithCodeRequest verifyRequest, HttpServletRequest request) {
        // TODO: consider demanding to send all of user device info which is required to generate fingerprint - this way we can generate fingerprint on our own in backend and check if it's the same as fingerprint sent by user

        Account account = accountRepository.findById(verifyRequest.getClientId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given clientId does not exist")
        );
        String requestPublicIp = HttpRequestUtils.getClientIpAddressFromRequest(request);
        List<Device> accountDevices = account.getAccountDevices();
        Device foundDevice = accountDevices.stream().filter(device -> device.getFingerprint()
                .equals(verifyRequest.getDeviceFingerprint())).findFirst().orElse(null);
        if (foundDevice == null) {
            throw new ResourceNotFoundException("There is no such device attached to this account");
        }
        if (foundDevice.isVerified()) {
            throw new IllegalArgumentException("This device is already verified");
        }
//        if (!foundDevice.getIp().equals(requestPublicIp)) {     // overkill?
//            throw new IllegalArgumentException("You cannot verify this device from different ip");
//        }
        boolean isCodeValid = otpService.verifyCode(verifyRequest.getCode(), account.getSecret());
        if (isCodeValid) {
            foundDevice.setVerified(true);
            deviceRepository.save(foundDevice);
        }
        return isCodeValid;
        //      jak bd coś takiego spr. to można dawać komunikaty o podejrzanej aktywności
        //            if (!account.isShouldBeVerified()) return false;
    }

    public String getLoginCombination(String clientId) {
        Account account = accountRepository.findById(clientId).
                orElseThrow(() -> new ResourceNotFoundException(NO_SUCH_ACCOUNT_MESSAGE + " with client id " + clientId));

        return account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition();
    }

    public static class AccountMapper {
        public static Account map(AccountRegistration registerData, Set<String> existingAccountsNumbers, Set<String> existingClientIds) {
            return new Account(
                    existingClientIds,
                    existingAccountsNumbers,
                    registerData.getPassword()
            );
        }
    }

    public static class AccountHashMapper {
        public static AccountHash map(PartPasswordHash partPasswordHash) {
            List<String> hashLocations = partPasswordHash.getDigitsLocations().stream().map(String::valueOf).collect(Collectors.toList());
            String passwordPartCharactersPosition = String.join(" ", hashLocations);
            return new AccountHash(
                    partPasswordHash.getHash(),
                    passwordPartCharactersPosition
            );
        }
    }

    @Override
    public Account loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findById(clientId).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Account %s not found", clientId)));
    }
}
