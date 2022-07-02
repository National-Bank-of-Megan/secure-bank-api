package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;   // 5 minutes

    @Id
    private Long clientId;

    @Column
    private String opt;

    @Column
    private Date otpRequestedTime;

    public Otp(Long clientId, String opt) {
        this.clientId = clientId;
        this.opt = opt;
        this.otpRequestedTime = new Date();
    }
}
