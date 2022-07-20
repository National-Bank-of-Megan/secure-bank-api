package pl.edu.pw.service.account;

import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData);

    Account getAccount(String accountNumber);

    String getLoginCombination(String username);

    boolean verify(VerifyCodeRequest request, HttpServletRequest httpRequest);
}
