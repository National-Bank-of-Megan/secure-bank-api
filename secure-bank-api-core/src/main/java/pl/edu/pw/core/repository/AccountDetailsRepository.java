package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.core.domain.AccountDetails;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Long> {
}
