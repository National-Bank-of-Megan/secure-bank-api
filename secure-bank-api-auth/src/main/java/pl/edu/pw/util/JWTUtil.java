package pl.edu.pw.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.JsonWebTokenType;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Data
@NoArgsConstructor
public class JWTUtil {

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getToken(Account user, HttpServletRequest request, JsonWebTokenType type) {

        Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret.getBytes());

        String token = JWT.create()
                .withSubject(user.getClientId())
                .withExpiresAt(new Date(System.currentTimeMillis() + (type == JsonWebTokenType.ACCESS ? this.jwtExpirationTime : this.refreshTokenExpirationTime)))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return token;
    }

    public Map<String, String> getTokensWithRefreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication();
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", this.getToken(account, request, JsonWebTokenType.ACCESS));
            tokens.put("refresh_token", this.getToken(account, request, JsonWebTokenType.REFRESH));
            return tokens;
        } else {
            throw new RuntimeException("There is no \"Bearer\" header in your request.");
        }
    }


}
