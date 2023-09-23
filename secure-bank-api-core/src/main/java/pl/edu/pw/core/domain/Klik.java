package pl.edu.pw.core.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.SECONDS;
import static pl.edu.pw.core.constant.Constants.KLIK_DURATION_SECONDS;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "account")
public class Klik {

    @Id
    private String clientId;

    @Column
    private String klikCode;

    @Column
    private LocalDateTime generateDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "client_id")
    private Account account;

    public Klik(String clientId) {
        this.clientId = clientId;
    }

    public boolean isValid() {
        if (Objects.isNull(klikCode) || Objects.isNull(generateDate)) {
            return false;
        }
        return SECONDS.between(generateDate, LocalDateTime.now()) <= KLIK_DURATION_SECONDS;
    }
}
