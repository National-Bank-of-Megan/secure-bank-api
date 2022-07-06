package pl.edu.pw.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private Date orderedOn;

    @Column
    @ManyToOne
    @JoinColumn(name="client_id",nullable=false)
    private Account receiver;

    @Column
    @ManyToOne
    @JoinColumn(name="client_id",nullable=false)
    private Account sender;

    @Column private double amount;
    @Column @Enumerated(EnumType.STRING) private Currency currency;
    @Column @Enumerated(EnumType.STRING) private TransferType transferType;

}
