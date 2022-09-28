package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

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
}
