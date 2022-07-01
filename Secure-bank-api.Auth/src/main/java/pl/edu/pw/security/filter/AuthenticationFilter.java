//package pl.edu.pw.security.filter;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.http.HttpServletRequest;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.AllArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import pl.edu.pw.user.Account;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//@AllArgsConstructor
//public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private AuthenticationManager authenticationManager;
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        String username, password;
//
//        try {
//            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
//            username = requestMap.get("username");
//            password = requestMap.get("password");
//        } catch (IOException e) {
//            throw new AuthenticationServiceException(e.getMessage(), e);
//        }
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
//        return authenticationManager.authenticate(authenticationToken);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        // TODO: zabezpieczyć sekret
//        Account user = (Account)authResult.getPrincipal();
//        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//        String token = JWT.create()
//                .withSubject(user.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))
//                .withIssuer(request.getRequestURL().toString())
////                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//                .withClaim("name", "accoutn name")
//                .withClaim("id", user.getAccountNumber())
//                .sign(algorithm);
//
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("access_token",token);
//        tokens.put("refresh_token",token);
//        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(),tokens);
//    }
//
//}
