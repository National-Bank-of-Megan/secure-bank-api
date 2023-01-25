package pl.edu.pw.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(RestAuthenticationFailureHandler.class);
    private static long LOCK_TIME = 86400000;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    private long MAX_LOGIN_ATTEMPTS = 5;

    public static boolean isAccountStillLocked(Account account) {
        long current = System.currentTimeMillis();
        if (account.getLockTime() == null) return false;
        long time = account.getLockTime().getTime();
        log.info("time " + time);
        log.info("current " + current);
        log.info("lock time " + LOCK_TIME);
        System.out.println(time + LOCK_TIME > current);
        return time + LOCK_TIME > current;
    }

    public static boolean isAccountLocked(Account account) {
        return !account.isAccountNonLocked() || isAccountStillLocked(account);
    }

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
                System.out.println(!isAccountStillLocked(a));
                if (!isAccountStillLocked(a)) {
                    accountService.unlockAccount(a);
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");


                    Map<String, String> error = new HashMap<>();
                    error.put("message", "You failed to login more than " + MAX_LOGIN_ATTEMPTS + " times. Your account has been locked for 24h for safety reasons");
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
//                    response.getWriter().write(mapper.write("You failed to login more than " + MAX_LOGIN_ATTEMPTS + " times. Your account has been locked for 24h for safety reasons"));
                    return;
                }
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
}
