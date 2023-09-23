package pl.edu.pw.auth.service.account;

import pl.edu.pw.auth.dto.AccountRegistration;
import pl.edu.pw.auth.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.core.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;

public interface AuthService {
    SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData, HttpServletRequest request);

    Account getAccount(String accountNumber);

    String getLoginCombination(String username);

    void setOtherHashCombination(Account account, SecureRandom secureRandom);
}
