package pl.edu.pw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table
@EqualsAndHashCode
public class AccountHash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_number")
    private Account account;

    @Column
    private String passwordPart;

    @Column
    private String passwordPartCharactersPosition;

    public AccountHash() {

    }

    public AccountHash(String passwordPart, String passwordPartCharactersPosition) {
        this.passwordPart = passwordPart;
        this.passwordPartCharactersPosition = passwordPartCharactersPosition;
    }
}
