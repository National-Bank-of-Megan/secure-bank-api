package pl.edu.pw.auth.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.dto.*;
import pl.edu.pw.auth.exception.DuplicateResourceException;
import pl.edu.pw.auth.exception.InvalidCredentialsException;
import pl.edu.pw.auth.exception.ResourceNotFoundException;
import pl.edu.pw.auth.repository.AccountRepository;
import pl.edu.pw.auth.service.otp.OtpService;
import pl.edu.pw.auth.util.PasswordUtil;
import pl.edu.pw.core.domain.*;
import pl.edu.pw.core.exception.InvalidCurrencyException;
import pl.edu.pw.core.exception.SubAccountNotFoundException;
import pl.edu.pw.core.repository.FavoriteReceiverRepository;
import pl.edu.pw.core.repository.SubAccountRepository;
import pl.edu.pw.core.util.CurrentUserUtil;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static pl.edu.pw.auth.service.account.AccountServiceImpl.AccountMapper.map;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final FavoriteReceiverRepository favoriteReceiverRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void addCurrencyBalance(Account account, AddCurrencyBalance addCurrencyBalance) {
        Currency currency;
        if (addCurrencyBalance.getAmount().doubleValue() <= 0.0) {
            throw new IllegalArgumentException("Amount to add cannot be lower or equal to 0");
        }
        try {
            currency = Currency.valueOf(addCurrencyBalance.getCurrency());
        } catch (NullPointerException e) {
            throw new InvalidCurrencyException("Currency " + addCurrencyBalance.getCurrency() + " not found");
        }

        SubAccount subAccount = subAccountRepository.findById(new SubAccountId(CurrentUserUtil.getCurrentUser(), currency)).orElseThrow(
                () -> new SubAccountNotFoundException("Subaccount was not found")
        );

        if (subAccount.getBalance().add(addCurrencyBalance.getAmount()).compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Cannot add more money to this subaccount");
        }

        subAccount.addToBalance(addCurrencyBalance.getAmount());
    }

    @Override
    public List<AccountCurrencyBalance> getAccountCurrenciesBalance(Account account) {
        List<AccountCurrencyBalance> accountCurrencyBalanceList = new ArrayList<>();
        Map<Currency, SubAccount> accountSubAccounts = account.getSubAccounts();
        for (Currency currency : accountSubAccounts.keySet()) {
            String currencyName = currency.name();
            BigDecimal balance = accountSubAccounts.get(currency).getBalance();
            accountCurrencyBalanceList.add(new AccountCurrencyBalance(currencyName, balance));
        }
        return accountCurrencyBalanceList;
    }

    @Override
    public List<FavoriteReceiverDTO> getAllFavoriteReceivers(Account loggedAccount) {
        Account account = accountRepository.findById(loggedAccount.getClientId()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + loggedAccount.getClientId() + " client id was not found"));
        return account.getFavoriteReceivers().stream().map(AccountMapper::map).toList();
    }

    @Override
    public FavoriteReceiverDTO addFavoriteReceiver(Account loggedAccount, AddFavoriteReceiver addFavoriteReceiver) {
        Account account = accountRepository.findById(loggedAccount.getClientId()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + loggedAccount.getClientId() + " client id was not found"));

        if (favoriteReceiverAlreadyAdded(account, addFavoriteReceiver)) {
            throw new DuplicateResourceException("Account number '"
                    + addFavoriteReceiver.getAccountNumber() + "' has already been added to favorite receivers");
        }

        FavoriteReceiver favoriteReceiver = map(addFavoriteReceiver);

        account.addFavoriteReceiver(favoriteReceiver);
        return map(favoriteReceiverRepository.save(favoriteReceiver));
    }

    private boolean favoriteReceiverAlreadyAdded(Account account, AddFavoriteReceiver favoriteReceiverToAdd) {
        return account.getFavoriteReceivers().stream()
                .map(FavoriteReceiver::getAccountNumber)
                .anyMatch(favoriteReceiverAccountNumber -> favoriteReceiverAccountNumber.equals(favoriteReceiverToAdd.getAccountNumber()));
    }

    @Override
    public AccountDTO getAccountData(Account loggedAccount) {
        Account account = accountRepository.findById(loggedAccount.getClientId()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + loggedAccount.getClientId() + " client id was not found"));
        return map(account);
    }

    public void resetLoginAttempts(Account account) {
        account.setLoginAttempts(0L);
        accountRepository.save(account);
    }

    @Override
    public void updateLoginAttempts(Account account, long attempts) {
        if (attempts < 0) throw new IllegalArgumentException("Number of login attempts should be positive");
        account.setLoginAttempts(attempts);
        accountRepository.save(account);
    }

    @Override
    public void lockAccount(Account account) {
        account.setAccountNonLocked(false);
        account.setLockTime(new Date());
        accountRepository.save(account);
    }

    @Override
    public void unlockAccount(Account account) {
        account.setAccountNonLocked(true);
        account.setLockTime(null);
        account.setLoginAttempts(0L);
        accountRepository.save(account);
    }

    @Override
    public void changePassword(Account loggedAccount, ChangePassword changePassword) {

        Account account = accountRepository.findById(loggedAccount.getClientId()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + loggedAccount.getClientId() + " client id was not found"));
        String accountSecret = account.getSecret();
        String currentHashedPassword = account.getPassword();

        if (!otpService.verifyCode(changePassword.getOtpCode(), accountSecret) ||
                !passwordEncoder.matches(changePassword.getOldPassword(), currentHashedPassword)) {
            log.error("Incorrect old password or one time password");
            throw new InvalidCredentialsException("Incorrect old password or one time password");
        }

        if (changePassword.getOldPassword().equals(changePassword.getNewPassword())) {
            log.error("New password cannot be the same as old password");
            throw new IllegalArgumentException("New password cannot be the same as old password");
        }

        String newPasswordHashed = passwordEncoder.encode(changePassword.getNewPassword());
        account.setPassword(newPasswordHashed);
        PasswordUtil.updateAccountHashes(account, changePassword.getNewPassword(), passwordEncoder);
    }


    public static class AccountMapper {
        public static FavoriteReceiverDTO map(FavoriteReceiver favoriteReceiver) {
            return new FavoriteReceiverDTO(
                    favoriteReceiver.getId(),
                    favoriteReceiver.getName(),
                    favoriteReceiver.getAccountNumber()
            );
        }

        public static FavoriteReceiver map(AddFavoriteReceiver addFavoriteReceiver) {
            return FavoriteReceiver.builder()
                    .name(addFavoriteReceiver.getName())
                    .accountNumber(addFavoriteReceiver.getAccountNumber())
                    .build();
        }

        public static AccountDTO map(Account account) {
            return new AccountDTO(
                    account.getClientId(),
                    account.getAccountNumber(),
                    account.getAccountDetails().getFirstName(),
                    account.getAccountDetails().getLastName(),
                    account.getAccountDetails().getEmail(),
                    account.getAccountDetails().getPhone()
            );
        }
    }
}
