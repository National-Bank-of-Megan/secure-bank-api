package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.edu.pw.domain.Account;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.security.config.BankGrantedAuthorities;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static pl.edu.pw.util.JWTUtil.TOKEN_PREFIX;

@Order(3)
public class MobileAuthorizationFilter extends AuthorizationFilterAbstract {

    private static final Logger log = LoggerFactory.getLogger(MobileAuthorizationFilter.class);

    public MobileAuthorizationFilter(AccountRepository accountRepository) {
        super(accountRepository);
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        try {
            log.info("getting mobile app JWT");
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            Algorithm algorithmMobile = Algorithm.RSA256(getRSAPublicKey(), null);
            JWTVerifier mobileVerifier = JWT.require(algorithmMobile).build();
            DecodedJWT decodedJWT = mobileVerifier.verify(token);
            String accountNumber = decodedJWT.getSubject().substring("auth0|".length());
            log.info("USER CREDENTIALS " + decodedJWT.getClaims());
            List<SimpleGrantedAuthority> userAuthorities = getUserAuthorities(String.valueOf(decodedJWT.getClaims().get("scope")));
            log.info(userAuthorities.toString());
            ClientIdContainer.clientId = accountNumber;
            Account account = accountRepository.findById(accountNumber).orElseThrow(() ->
                    new ResourceNotFoundException("Account with " + accountNumber + " account number was not found"));
            return new UsernamePasswordAuthenticationToken(account, decodedJWT.getClaims(), userAuthorities);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private RSAPublicKey getRSAPublicKey() throws CertificateException {
        String publicKeyString = "MIIDDTCCAfWgAwIBAgIJAXpCTsY0kd06MA0GCSqGSIb3DQEBCwUAMCQxIjAgBgNVBAMTGWRldi14a210aHZzdy51cy5hdXRoMC5jb20wHhcNMjIwOTIyMjA0MDEwWhcNMzYwNTMxMjA0MDEwWjAkMSIwIAYDVQQDExlkZXYteGttdGh2c3cudXMuYXV0aDAuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyE21Kkl82fmE/4ck/PZdSRrt3aTHH35yOYxF/PPIwjiB7zM3w+G8V6YAciQHtxkx4oCU4zqKK5iPAAjxksMzbdE9St9xknqiDlACsrR1hAU+v/W3nzN7FVPNLWG+kWh9egZQSajGlUsc6d/oEBbnNOIxtRy70OWNn/tRE3QccoKnYViZrdqfmrFv8aB24lloYt+DYLWpkuvG1CzuTtZfiJ8ROomrYypnv5ZDlAHEkg+ceAbVxMRIamXVxQVypof4Fzx8sN8cFLC/dPgLxOwkxbn6Qbc0gAS8EqwNykRFIsPWfzf0BLxLYv8Ul/IAWJv0sOuccJ9zuHIv6OY6HRvNLwIDAQABo0IwQDAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBQO0zN5eArmD+YqOslgVeHq8LLTZzAOBgNVHQ8BAf8EBAMCAoQwDQYJKoZIhvcNAQELBQADggEBAJK9/bvM/SqQNlrcNVsVRack0QGM0GQGNELUvoCPxaxiFsS3JNtL2REgkLIgyAsCZBTlpdLz3gGLipubeh67boaVp0N0/80Bl7ODnyHuRfBRu5FVAs+qe296rc4YSEtaNkElVbHxTrAkqFxAQ4/CdWR/WoRivcodBTsCO6uraGoZWb86OcECMywg3P/SWvOQ4oAsCxs0QPdu3mXKJ+3CLafqERm9sa75cFWcduhc3ZrfWtJLIzgMFialaYA0MJDfXMw/YGvsaAgkF2IDHbdDdquhvFjXAZxFvncdQENI5syQrPdMtNOAGwuNvT4zxLS1taiiFJOgcgHCYLUVZr/0R8Y=";
        var decode = Base64.getDecoder().decode(publicKeyString);
        var certificate = CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(decode));
        var publicKey = (RSAPublicKey) certificate.getPublicKey();
        return publicKey;
    }
}
