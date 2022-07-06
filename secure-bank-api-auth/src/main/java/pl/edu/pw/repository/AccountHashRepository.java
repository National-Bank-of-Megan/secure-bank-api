package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.domain.AccountHash;

import java.util.List;

@Repository
public interface AccountHashRepository extends JpaRepository<AccountHash, Long> {

    List<AccountHash> findAllByAccountAccountNumber(String accountNumber);
}
