package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.SubAccount;
import pl.edu.pw.domain.SubAccountId;

import java.util.Optional;

public interface SubAccountRepository extends JpaRepository<SubAccount, SubAccountId> {

    Optional<SubAccount> findById(SubAccountId id);

}
