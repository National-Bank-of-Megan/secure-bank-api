package pl.edu.pw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
public abstract class MoneyBalanceOperation implements Comparable<MoneyBalanceOperation> {

    private Long id;
    private LocalDateTime requestDate;

    @Override
    public int compareTo(MoneyBalanceOperation moneyBalanceOperation) {
        return requestDate.compareTo(moneyBalanceOperation.getRequestDate());
    }
}
