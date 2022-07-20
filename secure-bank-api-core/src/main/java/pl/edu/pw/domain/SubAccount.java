package pl.edu.pw.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class SubAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Account account;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column
    private double balance;

    public SubAccount(Account account, Currency currency) {
        this.account = account;
        this.currency = currency;
    }

    public void addToBalance(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}
