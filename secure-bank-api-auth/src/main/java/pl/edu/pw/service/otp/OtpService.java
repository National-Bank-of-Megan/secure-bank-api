package pl.edu.pw.service.otp;

import pl.edu.pw.user.Account;

public interface OtpService {

    String generateOneTimePassword(Account account);
    void sendOtpEmail(Account account, String otp);
    void clearOneTimePassword(Account account);
    void verify(String otp);
}
