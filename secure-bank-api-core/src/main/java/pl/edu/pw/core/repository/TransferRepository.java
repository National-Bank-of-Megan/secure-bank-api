package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.core.domain.Transfer;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findTop5ByReceiverClientIdOrSenderClientIdOrderByRequestDateDesc(String receiverId, String senderId);

    List<Transfer> findAllByReceiverClientIdOrSenderClientId(String receiverId, String senderId);
}
