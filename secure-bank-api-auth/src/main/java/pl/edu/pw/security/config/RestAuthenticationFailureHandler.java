package pl.edu.pw.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(RestAuthenticationFailureHandler.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private long MAX_LOGIN_ATTEMPTS = 5;
    private long LOCK_TIME = 86400000;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        exception = new BadCredentialsException("Invalid credentials");
        //        cast request to custom request wrapper that retrieves body
        CustomHttpServletRequestWrapper req = (CustomHttpServletRequestWrapper) request;
        //        save json entries in map
        Map<String, Object> jsonRequest = new ObjectMapper().readValue(req.getBody(), Map.class);
        String clientId = (String) jsonRequest.get("clientId");

        Optional<Account> account = accountRepository.findById(clientId);

        if (account.isPresent()) {
            log.info("account with id " + clientId + " is present");
            Account a = account.get();

            if (!a.isAccountNonLocked()) {
                log.info("account is  locked");

                if (!isAccountStillLocked(a)) {
                    accountService.unlockAccount(a);
                } else
                    exception = new LockedException("You failed to login more than " + MAX_LOGIN_ATTEMPTS + " times." +
                            " Your account has been locked for 24h for safety reasons");

            }

            if (a.isAccountNonLocked()) {
                log.info("updating login attempts");
                accountService.updateLoginAttempts(a, a.getLoginAttempts() + 1);

                if (a.getLoginAttempts() == MAX_LOGIN_ATTEMPTS) {
                    accountService.lockAccount(a);
                }
            }

        }
        super.onAuthenticationFailure(request, response, exception);


    }

    private boolean isAccountStillLocked(Account account) {
        long current = System.currentTimeMillis();
        long time = account.getLockTime().getTime();
        return time + LOCK_TIME > current;
    }
}
