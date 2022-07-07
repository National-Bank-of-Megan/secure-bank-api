package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.domain.Account;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AuthorizationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";

    private AccountRepository accountRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthorizationFilter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override // według moich obliczeń powinno działać, a kod jest bardziej clean
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthorizationFilter->\ttrying to authorize (jwt)...");
        if (!(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh"))) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                log.info("AuthorizationFilter->\tchecking jwt");
                try {
                    UsernamePasswordAuthenticationToken auth = getAuthentication(authorizationHeader);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    e.printStackTrace();
                    Map<String, String> error = getErrorMap(e);
                    response.setStatus(FORBIDDEN.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String accountNumber = decodedJWT.getSubject();
        Account account = accountRepository.findByClientId(Long.valueOf(accountNumber)).orElseThrow();
        return new UsernamePasswordAuthenticationToken(account, decodedJWT.getClaims(), null);
    }

    private Map<String, String> getErrorMap(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        return error;
    }
}
