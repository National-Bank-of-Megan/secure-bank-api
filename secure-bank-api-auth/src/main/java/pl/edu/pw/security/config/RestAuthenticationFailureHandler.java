package pl.edu.pw.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Account;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.account.AccountService;
import pl.edu.pw.util.http.CustomHttpServletRequestWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(RestAuthenticationFailureHandler.class);

    @Autowired
    private AccountRepository accountRepository;

    @Value("${auth.maxFailureAttempts}")
    private long MAX_LOGIN_ATTEMPTS;

    @Value(("${auth.lockTime}"))
    private long LOCK_TIME;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("Inside...");
        StringBuilder body = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        exception = new BadCredentialsException("Invalid credentials");
        log.info("client id:\t"+body);

        Optional<Account> account = accountRepository.findById(request.getParameter("clientId"));

        if(account.isPresent()){
            Account a = account.get();

            if(!a.isAccountNonLocked()) {

                if (!isAccountStillLocked(a)){
                    a.setLoginAttempts(0l);
                    a.setAccountNonLocked(true);
                }
                else
                    exception = new LockedException("You failed to login more than " + MAX_LOGIN_ATTEMPTS + " times." +
                            " Your account has been locked for 24h for safety reasons");

            }

            if(a.isAccountNonLocked()){

                a.setLoginAttempts(a.getLoginAttempts()+1);

                if(a.getLoginAttempts() == MAX_LOGIN_ATTEMPTS){
                    a.setAccountNonLocked(false);
                }
            }

        }
        super.onAuthenticationFailure(request, response, exception);


    }

    private boolean isAccountStillLocked(Account account){
        long current = System.currentTimeMillis();
        long time = account.getLockTime().getTime();
        return time + LOCK_TIME > current;
    }
}
