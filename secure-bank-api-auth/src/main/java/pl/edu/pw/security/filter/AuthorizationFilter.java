package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.util.http.CustomHttpServletRequestWrapper;

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
import static pl.edu.pw.util.JWTUtil.TOKEN_PREFIX;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);
    private final AccountRepository accountRepository;
    private final String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthorizationFilter->\ttrying to authorize (jwt)...");
        if (!(request.getServletPath().equals("/api/web/login") || !(request.getServletPath().equals("/api/mobile/login")) || request.getServletPath().equals("/api/web/login/verify"))) {
            String authorizationHeader = null;
//           potential security flaw
            if (request.getServletPath().contains("/api/transfer/notification/subscribe")) {
                authorizationHeader = "Bearer " + request.getParameter("jwt");
                log.info("/api/transfer/notification/subscribe received JWT -> " + authorizationHeader);
            } else
                authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                log.info("AuthorizationFilter->\tchecking jwt");
                try {
                    UsernamePasswordAuthenticationToken auth = getAuthentication(authorizationHeader);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    e.printStackTrace();
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
        }
        filterChain.doFilter(new CustomHttpServletRequestWrapper(request), response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String accountNumber = decodedJWT.getSubject();
        Account account = accountRepository.findById(accountNumber).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + accountNumber + " account number was not found"));
        return new UsernamePasswordAuthenticationToken(account, decodedJWT.getClaims(), null);
    }
}
