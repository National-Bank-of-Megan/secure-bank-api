package pl.edu.pw.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
public class CurrencyExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordered_on")
    private LocalDateTime orderedOn;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currencyBought;

    @Column
    private BigDecimal amountBought;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currencySold;

    @Column
    private BigDecimal amountSold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Account account;

    public CurrencyExchange(LocalDateTime orderedOn, Currency currencyBought, BigDecimal amountBought, Currency currencySold, BigDecimal amountSold, Account account) {
        this.orderedOn = orderedOn;
        this.currencyBought = currencyBought;
        this.amountBought = amountBought;
        this.currencySold = currencySold;
        this.amountSold = amountSold;
        this.account = account;
    }
}
