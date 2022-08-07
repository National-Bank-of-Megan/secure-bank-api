package pl.edu.pw.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Data
@NoArgsConstructor
@Log4j2
public class JWTUtil {
    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getToken(Account user, HttpServletRequest request, JsonWebTokenType tokenType) {

        Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret.getBytes());

        // todo: remove firstName and lastName from refreshToken
        String token = JWT.create()
                .withSubject(user.getClientId())
                .withClaim(TOKEN_TYPE_CLAIM, tokenType.name())
                .withClaim("firstName", user.getAccountDetails().getFirstName())
                .withClaim("lastName", user.getAccountDetails().getLastName())
                .withExpiresAt(new Date(System.currentTimeMillis() + (tokenType == JsonWebTokenType.ACCESS ? this.jwtExpirationTime : this.refreshTokenExpirationTime)))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return token;
    }

    public Map<String, String> getAuthTokenByRefreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            DecodedJWT decodedJWTToken = getValidJWTToken(authorizationHeader);
            JsonWebTokenType decodedJWTType = JsonWebTokenType.valueOf(decodedJWTToken.getClaim(TOKEN_TYPE_CLAIM).asString());
            if (!decodedJWTType.equals(JsonWebTokenType.REFRESH)) {
                throw new JWTVerificationException("Provided token is not a refresh token.");
            }
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", this.getToken(account, request, JsonWebTokenType.ACCESS));
            return tokens;
        } else {
            throw new RuntimeException("There is no \"Bearer\" header in your request.");
        }
    }

    private DecodedJWT getValidJWTToken(String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
