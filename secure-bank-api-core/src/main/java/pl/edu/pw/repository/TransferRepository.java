package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
