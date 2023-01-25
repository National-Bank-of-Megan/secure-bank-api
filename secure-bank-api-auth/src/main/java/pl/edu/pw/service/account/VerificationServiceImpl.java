package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.VerifyDeviceWithCodeRequest;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private static final Logger log = LoggerFactory.getLogger(VerificationServiceImpl.class);
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final DevicesService devicesService;
    private final AuthService authService;

    @Override
    public boolean verifyDevice(VerifyDeviceWithCodeRequest verifyRequest, HttpServletRequest request) {
        Account account = accountRepository.findById(verifyRequest.getClientId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given clientId does not exist")
        );
        String requestPublicIp = HttpRequestUtils.getClientIpAddressFromRequest(request);
        List<Device> accountDevices = account.getAccountDevices();
        Device foundDevice = accountDevices.stream().filter(device -> device.getFingerprint()
                .equals(verifyRequest.getDeviceFingerprint())).findFirst().orElse(null);
        if (foundDevice != null) {
            throw new IllegalArgumentException("This device is already verified");
        }

        boolean isCodeValid = otpService.verifyCode(verifyRequest.getCode(), account.getSecret());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                verifyRequest.getClientId(), verifyRequest.getPassword()
        );
        boolean newDeviceVerified = isCodeValid && authenticationManager.authenticate(authentication) != null;

        if (newDeviceVerified) {
            String deviceName = HttpRequestUtils.getDeviceNameFromRequest(request, devicesService);
            Device trustedDevice = new Device(verifyRequest.getDeviceFingerprint(), deviceName, LocalDateTime.now(), requestPublicIp);
            trustedDevice.setLastLoggedIn(LocalDateTime.now());
            devicesService.saveDevice(verifyRequest.getClientId(), trustedDevice);
            authService.setOtherHashCombination(account, new SecureRandom());
        }
        return newDeviceVerified;
    }
}
