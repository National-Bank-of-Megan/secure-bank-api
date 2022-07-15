package pl.edu.pw.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JWTUtil {

    @Value("${jwt.expirationTime}")
    private static long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private static long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private static String jwtSecret;

    public static String getToken(Account user, HttpServletRequest request) {

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        String token = JWT.create()
                .withSubject(user.getClientId())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return token;
    }

    public static Map<String, String> getTokensWithRefreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication();
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", JWTUtil.getToken(account, request));
            tokens.put("refresh_token", JWTUtil.getToken(account, request));
            return tokens;
        } else {
            throw new RuntimeException("There is no \"Bearer\" header in your request.");
        }
    }


}
