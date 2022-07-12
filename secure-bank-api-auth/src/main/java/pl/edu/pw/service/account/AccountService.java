package pl.edu.pw.service.account;

import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AccountService {
    String registerAccount(AccountRegistration registerData);
    Account getAccount(String accountNumber);
    String getLoginCombination(String username);
//    void login (LoginRequest request);
    boolean verify(VerifyCodeRequest request, HttpServletRequest httpRequest);
    Map<String,String> getTokensWithRefreshToken(HttpServletRequest request);
}
