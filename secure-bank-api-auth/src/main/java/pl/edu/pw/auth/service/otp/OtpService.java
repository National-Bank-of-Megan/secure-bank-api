package pl.edu.pw.auth.service.otp;

public interface OtpService {
    String generateSecret();

    String getUriForImage(String secret);

    boolean verifyCode(String code, String secret);
}
