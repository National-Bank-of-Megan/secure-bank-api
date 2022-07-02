package pl.edu.pw.service.otp;

import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.domain.Otp;
import pl.edu.pw.repository.OtpRepository;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.user.Account;

import java.util.Optional;

@AllArgsConstructor
public class OtpServiceImpl implements OtpService{

    private PasswordEncoder passwordEncoder;
    private OtpRepository otpRepository;
    private EmailSenderServiceImpl emailSenderService;


    @Override
    public String generateOneTimePassword(Account account) {
        String OTP = RandomString.make(8);
        String encodedOTP = passwordEncoder.encode(OTP);
        Otp o = otpRepository.findById(account.getClientId()).orElseThrow(
                ()-> new RuntimeException("Duplicate otp exception")
        );
        otpRepository.save(new Otp(account.getClientId(),encodedOTP));
        return encodedOTP;
    }

    @Override
    public void sendOtpEmail(Account account, String otp) {
//        todo add account email
        emailSenderService.send("user@email.com",otp);
    }

    @Override
    public void clearOneTimePassword(Account account) {
        Otp otp = otpRepository.findById(account.getClientId()).orElseThrow(
                ()-> new RuntimeException("Otp not found")
        );
        otpRepository.delete(otp);
    }
}
