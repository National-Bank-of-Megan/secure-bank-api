package pl.edu.pw.service.otp;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Otp;
import pl.edu.pw.repository.OtpRepository;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.domain.Account;

import java.util.Date;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

//todo convert ot record
@RequiredArgsConstructor
@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailSenderServiceImpl emailSenderService;

    @Override
    public String generateSecret() {
        SecretGenerator generator = new DefaultSecretGenerator();
        return generator.generate();
    }

    @Override
    public String getUriForImage(String secret) {
        QrData data = new QrData.Builder()
                .label("One Time Password")
                .secret(secret)
                .issuer("National-Bank-of-Megan")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];

        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            throw new RuntimeException("Could not generate QR code");
        }

        String mimeType = generator.getImageMimeType();

        String dataUriForImage = getDataUriForImage(imageData, mimeType);
        return dataUriForImage;
    }

    @Override
    public boolean verifyCode(String code, String secret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        boolean isValid = verifier.isValidCode(secret,code);
        return isValid;
    }

    @Override
    public String generateOneTimePassword(Account account) {
        String OTP = RandomString.make(8);

        otpRepository.findByClientId(account.getClientId())
                .ifPresentOrElse(
                        otp -> {
                            otp.setOtp(OTP);
                            otp.setOtpRequestedTime(new Date());
                            otpRepository.save(otp);
                        },
                        () -> {
                            otpRepository.save(new Otp(account.getClientId(), OTP, new Date()));
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
        Otp otp = otpRepository.findByClientId(account.getClientId()).orElseThrow(
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
