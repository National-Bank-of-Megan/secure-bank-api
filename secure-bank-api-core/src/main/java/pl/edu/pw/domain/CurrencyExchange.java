package pl.edu.pw.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class CurrencyExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date orderedOn;

    @Column @Enumerated(EnumType.STRING) private Currency currencyBought;
    @Column private double amountBought;
    @Column @Enumerated(EnumType.STRING) private Currency currencySold;
    @Column private double amountSold;

    @ManyToOne
    @JoinColumn(name="client_id",nullable=false)
    private Account account;
}
