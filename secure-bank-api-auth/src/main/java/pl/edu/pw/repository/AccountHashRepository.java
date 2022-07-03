package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.user.AccountHash;

@Repository
public interface AccountHashRepository extends JpaRepository<AccountHash, Long> {
}
