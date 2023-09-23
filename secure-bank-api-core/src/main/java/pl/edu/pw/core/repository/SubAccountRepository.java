package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.core.domain.SubAccount;
import pl.edu.pw.core.domain.SubAccountId;

import java.util.Optional;

public interface SubAccountRepository extends JpaRepository<SubAccount, SubAccountId> {

    Optional<SubAccount> findById(SubAccountId id);

}
