package pl.edu.pw.logic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.Status;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingTransfer {
    private Long id;
    private Currency currency;
    private BigDecimal amount;
    private Status transferStatus;
}
