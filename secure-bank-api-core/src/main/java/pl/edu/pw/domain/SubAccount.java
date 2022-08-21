package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubAccount {

    @EmbeddedId
    private SubAccountId id;

    @Column
    private BigDecimal balance;

    public void addToBalance(BigDecimal amount) {
        if (amount.doubleValue() > 0.0) {
            balance = balance.add(amount);
        }
    }

    public void chargeFromBalance(BigDecimal amount) {
        if (balance.subtract(amount).doubleValue() < 0.0) {
            throw new IllegalArgumentException("Insufficient amount of funds on the account.");
        }
        if (amount.doubleValue() > 0.0) {
            balance = balance.subtract(amount);
        }
    }
}
