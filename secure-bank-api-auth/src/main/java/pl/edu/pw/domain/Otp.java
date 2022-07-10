package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.service.otp.OtpService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Otp {
    
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;   // 5 minutes

    @Id
    private Long clientNumber;

    @Column
    private String otp;

    @Column
    private Date otpRequestedTime;

    public Otp(Long clientNumber, String otp) {
        this.clientNumber = clientNumber;
        this.otp = otp;
        this.otpRequestedTime = new Date();
    }

    public Otp(String otp, Date otpRequestedTime) {
        this.otp = otp;
        this.otpRequestedTime = otpRequestedTime;
    }

    public boolean isValid(){
        return this.otpRequestedTime.getTime() + OTP_VALID_DURATION >= System.currentTimeMillis();
    }
}
