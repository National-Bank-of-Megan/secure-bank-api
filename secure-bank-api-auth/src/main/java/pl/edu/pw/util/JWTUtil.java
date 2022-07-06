package pl.edu.pw.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JWTUtil {

    public static String generateToken(Account user, int duration, HttpServletRequest request) {

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String token = JWT.create()
                .withSubject(user.getClientId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return token;
    }


}
