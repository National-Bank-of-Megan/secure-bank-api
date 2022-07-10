package pl.edu.pw.service.otp;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Otp;
import pl.edu.pw.repository.OtpRepository;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.domain.Account;

import java.util.Date;

//todo convert ot record
@RequiredArgsConstructor
@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailSenderServiceImpl emailSenderService;

    @Override
    public String generateOneTimePassword(Account account) {
        String OTP = RandomString.make(8);

        otpRepository.findByClientId(account.getClientNumber())
                .ifPresentOrElse(
                        otp -> {
                            otp.setOtp(OTP);
                            otp.setOtpRequestedTime(new Date());
                            otpRepository.save(otp);
                        },
                        () -> {
                            otpRepository.save(new Otp(account.getClientNumber(), OTP, new Date()));
                        }
                );

        return OTP;
    }

    @Override
    public void sendOtpEmail(Account account, String otp) {
//        todo add account email
        emailSenderService.send("user@email.com", otp);
    }

    @Override
    public void clearOneTimePassword(Account account) {
        Otp otp = otpRepository.findById(account.getClientNumber()).orElseThrow(
                () -> new RuntimeException("Otp not found")
        );
        otpRepository.delete(otp);
    }

    @Override
    public boolean verify(String otp) {
        Otp o = otpRepository.findByOtp(otp).orElseThrow(
                ()-> new IllegalArgumentException("Code not found")
        );
        return o.isValid();
    }
}
