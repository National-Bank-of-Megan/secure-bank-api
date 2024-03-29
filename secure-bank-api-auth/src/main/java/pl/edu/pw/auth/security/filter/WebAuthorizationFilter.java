package pl.edu.pw.auth.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.edu.pw.auth.exception.ResourceNotFoundException;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.auth.repository.AccountRepository;

import java.util.List;

import static pl.edu.pw.auth.util.JWTUtil.TOKEN_PREFIX;

@Order(1)
public class WebAuthorizationFilter extends AuthorizationFilterAbstract {

    private static final Logger log = LoggerFactory.getLogger(WebAuthorizationFilter.class);
    private final String jwtSecret;

    public WebAuthorizationFilter(AccountRepository accountRepository, String jwtSecret) {
        super(accountRepository);
        this.jwtSecret = jwtSecret;
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        try {
            log.info("getting web app JWT");
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String accountNumber = decodedJWT.getSubject();
            clientId = accountNumber;
            log.info("USER CREDENTIALS " + decodedJWT.getClaims());
            List<SimpleGrantedAuthority> userAuthorities = getUserAuthorities(String.valueOf(decodedJWT.getClaims().get("scope")));
            log.info(userAuthorities.toString());
            Account account = accountRepository.findById(accountNumber).orElseThrow(() ->
                    new ResourceNotFoundException("Account with " + accountNumber + " account number was not found"));
            return new UsernamePasswordAuthenticationToken(account, decodedJWT.getClaims(), userAuthorities);
        } catch (AlgorithmMismatchException e) {
            return null;
        }
    }
}
