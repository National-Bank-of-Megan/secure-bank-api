package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.core.domain.CurrencyExchange;

import java.util.List;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {
    List<CurrencyExchange> findTop5ByAccountClientIdOrderByOrderedOnDesc(String clientId);
}
