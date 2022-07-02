package pl.edu.pw.service.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailSenderServiceImpl {

    private final JavaMailSender mailSender;

    @Async
    public void send(String receiver, String code) {

        try {
            String email = buildEmail(code);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(receiver);
            helper.setSubject("Please verify your device");
            helper.setFrom("nbm_security_team@pw.edu.pl");
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }

    private String buildEmail(String code) {
        return "<div> <h1> You are trying to add a new device. Your verification code:</h1> <p>"+code+"</p></div>";
    }

}
