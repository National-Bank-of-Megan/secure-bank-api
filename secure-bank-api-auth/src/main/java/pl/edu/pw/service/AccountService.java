package pl.edu.pw.service;

import org.springframework.stereotype.Service;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.LoginRequest;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;
import java.util.Map;

public interface AccountService {

    Account getAccount(String accountNumber);
//    void login (LoginRequest request);
    Map<String,String> verify(VerifyCodeRequest request);
}
