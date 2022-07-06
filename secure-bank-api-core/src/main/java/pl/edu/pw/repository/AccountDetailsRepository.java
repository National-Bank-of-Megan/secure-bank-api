package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.AccountDetails;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails,Long> {
}
