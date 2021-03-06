package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubAccount {

    @EmbeddedId
    private SubAccountId id;

    @Column
    private double balance;

    public void addToBalance(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}
