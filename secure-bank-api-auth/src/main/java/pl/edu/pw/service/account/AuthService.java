package pl.edu.pw.service.account;

import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.dto.VerifyDeviceWithCodeRequest;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.List;

public interface AuthService {
    SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData, HttpServletRequest request);
    Account getAccount(String accountNumber);
    String getLoginCombination(String username);
    void setOtherHashCombination(Account account, SecureRandom secureRandom);
}
