package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.CurrencyExchange;
import pl.edu.pw.domain.Transfer;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findTop5ByReceiverClientIdOrSenderClientIdOrderByRequestDateDesc(String receiverId, String senderId);

}
