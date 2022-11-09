package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import pl.edu.pw.domain.Account;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;

import static pl.edu.pw.util.JWTUtil.TOKEN_PREFIX;

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
            Account account = accountRepository.findById(accountNumber).orElseThrow(() ->
                    new ResourceNotFoundException("Account with " + accountNumber + " account number was not found"));
            return new UsernamePasswordAuthenticationToken(account, decodedJWT.getClaims(), null);
        } catch (AlgorithmMismatchException e) {
            return null;
        }
    }
}
