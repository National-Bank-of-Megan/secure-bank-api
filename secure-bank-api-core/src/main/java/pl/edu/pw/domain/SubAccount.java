package pl.edu.pw.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class SubAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    Account account;

    @Column
    @Enumerated(EnumType.STRING)
    Currency currency;

    @Column
    private double balance;

    public SubAccount(Account account, Currency currency) {
        this.account = account;
        this.currency = currency;
    }
}
