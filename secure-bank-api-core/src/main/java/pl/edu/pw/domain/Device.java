package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fingerprint;

    @Column
    private String name;

    @Column
    private String ip;

    @Column
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastLoggedIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Account account;

    public Device(String fingerprint, String name, LocalDateTime registrationDate, String ip) {
        this.fingerprint = fingerprint;
        this.name = name;
        this.registrationDate = registrationDate;
        this.ip = ip;
    }
}
