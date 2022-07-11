package pl.edu.pw.service.otp;

import pl.edu.pw.domain.Account;

public interface OtpService {

    String generateOneTimePassword(Account account);
    void sendOtpEmail(Account account, String otp);
    void clearOneTimePassword(Account account);
    boolean verify(String otp);

    String generateSecret();
    String getUriForImage(String secret);
    boolean verifyCode(String code, String secret);



}
