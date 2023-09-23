package pl.edu.pw.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer implements Comparable<Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private LocalDateTime requestDate;

    @Column
    private LocalDateTime doneDate;

    @Column
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    @Override
    public int compareTo(Transfer transfer) {
        return getRequestDate().compareTo(transfer.getRequestDate());
    }
}
