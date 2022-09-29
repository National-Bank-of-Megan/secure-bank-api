package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.account.AuthService;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.util.JWTUtil;

import javax.persistence.EntityManager;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class MobileAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(MobileAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final DevicesService devicesService;
    private final SecureRandom random;
    private final JWTUtil jwtUtil;
    private final EntityManager entityManager;
    private final AuthService authService;

    public MobileAuthenticationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository,
                                   DevicesService devicesService, JWTUtil jwtUtil, EntityManager entityManager,
                                   AuthService authService) {

        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.devicesService = devicesService;
        this.authService = authService;
        this.random = new SecureRandom();
        this.jwtUtil = jwtUtil;
        this.entityManager = entityManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("MobileAuthenticationFilter->\ttrying to authenticate...");

        String username, password;
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            username = requestMap.get("clientId");
            password = requestMap.get("password");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        log.info("mobile->getting user token");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        log.info("mobile->manager");
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

    }


}
