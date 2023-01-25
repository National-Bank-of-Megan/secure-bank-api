package pl.edu.pw.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.security.config.BankGrantedAuthorities;
import pl.edu.pw.util.http.CustomHttpServletRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.edu.pw.util.JWTUtil.TOKEN_PREFIX;

@RequiredArgsConstructor
public abstract class AuthorizationFilterAbstract extends ClientIdContainer {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilterAbstract.class);
    protected final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthorizationFilter->\ttrying to authorize (jwt)...");
        log.info(request.getHeader("Authorization"));

        if (!(request.getServletPath().equals("/api/web/login") || request.getServletPath().equals("/api/web/login/verify") || request.getServletPath().contains("/payment/finalize"))) {
            String authorizationHeader = null;
//           potential security flaw
            if (request.getServletPath().contains("/api/transfer/notification/subscribe")) {
                authorizationHeader = "Bearer " + request.getParameter("jwt");
                log.info("/api/transfer/notification/subscribe received JWT -> " + authorizationHeader);
            } else
                authorizationHeader = request.getHeader(AUTHORIZATION);
            log.info(authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                log.info("AuthorizationFilter->\tchecking jwt");
                try {
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken auth = getAuthentication(authorizationHeader);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
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

    protected List<SimpleGrantedAuthority> getUserAuthorities(String scope) {
        return Arrays.stream(BankGrantedAuthorities.values()).filter(
                (authority) -> scope.contains(authority.toString().toLowerCase())
        ).collect(Collectors.toList()).stream().map(
                a -> new SimpleGrantedAuthority(a.toString())
        ).collect(Collectors.toList());
    }

    public abstract UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader);
}
