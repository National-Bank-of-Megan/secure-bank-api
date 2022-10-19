package pl.edu.pw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table
@EqualsAndHashCode
public class AccountHash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
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
