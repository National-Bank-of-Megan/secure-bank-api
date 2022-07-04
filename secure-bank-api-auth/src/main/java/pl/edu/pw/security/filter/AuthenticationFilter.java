package pl.edu.pw.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.user.Account;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private EmailSenderServiceImpl emailSenderService;
    private OtpService otpService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username, password;
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            username = requestMap.get("username");
            password = requestMap.get("password");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        Account account = (Account)authResult.getPrincipal();
////        todo change receiver from client id to email
        emailSenderService.send(account.getUsername(),otpService.generateOneTimePassword(account));
//        System.out.println("Old successful authentiation");
//        Account account = (Account)authResult.getPrincipal();
//        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//        String token = JWT.create()
//                .withSubject(account.getClientId().toString())
//                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))
//                .withIssuer(request.getRequestURL().toString())
//                .sign(algorithm);
//
//        String refreshToken = JWT.create().withSubject(account.getClientId().toString())
//                .withExpiresAt(new Date(System.currentTimeMillis()+1000*120*60))
//                .withIssuer(request.getRequestURL().toString())
//                .sign(algorithm);
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("access_token",token);
//        tokens.put("refresh_token",refreshToken);
//        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(),tokens);
    }


}
