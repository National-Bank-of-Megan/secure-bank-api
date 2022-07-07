package pl.edu.pw.service.account;

import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AccountService {
    void registerAccount(AccountRegistration registerData);
    Account getAccount(String accountNumber);
    String getLoginCombination(String username);
    Account getAccountLazy(String accountNumber);
//    void login (LoginRequest request);
    Map<String,String> verify(VerifyCodeRequest request, HttpServletRequest httpRequest);
    Map<String,String> getTokensWithRefreshToken(HttpServletRequest request);
}
