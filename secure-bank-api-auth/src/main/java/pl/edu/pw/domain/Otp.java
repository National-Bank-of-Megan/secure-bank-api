package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long clientId;

    @Column
    private String otp;

    @Column
    private Date otpRequestedTime;

    public Otp(Long clientId, String otp) {
        this.clientId = clientId;
        this.otp = otp;
        this.otpRequestedTime = new Date();
    }

    public Otp(String otp, Date otpRequestedTime) {
        this.otp = otp;
        this.otpRequestedTime = otpRequestedTime;
    }
}
