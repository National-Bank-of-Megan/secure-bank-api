package pl.edu.pw.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date requestDate;

    @Column
    private Date doneDate;

    @Column
    private Status status;

    @Column
    private String receiverName;

    @ManyToOne
//    @JoinColumn(name="client_id",nullable=false)
    private Account receiver;

    @ManyToOne
//    @JoinColumn(name="client_id",nullable=false)
    private Account sender;

    @Column
    private double amount;
    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column
    @Enumerated(EnumType.STRING)
    private TransferType transferType;
}
