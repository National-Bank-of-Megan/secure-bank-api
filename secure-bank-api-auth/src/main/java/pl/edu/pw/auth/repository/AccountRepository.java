package pl.edu.pw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.core.domain.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountDetailsEmail(String email);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByKlikKlikCode(String klikCode);
}
