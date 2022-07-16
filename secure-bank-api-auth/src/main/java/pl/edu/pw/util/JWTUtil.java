package pl.edu.pw.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JWTUtil {

    public static String generateToken(String secret, long expirationTime, Account user, HttpServletRequest request) {

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String token = JWT.create()
                .withSubject(user.getClientId())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return token;
    }


}
