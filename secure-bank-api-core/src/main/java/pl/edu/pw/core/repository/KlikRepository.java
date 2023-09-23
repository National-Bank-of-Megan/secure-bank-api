package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.core.domain.Klik;

import java.util.Optional;

@Repository
public interface KlikRepository extends JpaRepository<Klik, String> {
    Optional<Klik> findByKlikCode(String klikCode);

    Klik getByClientId(String clientId);
}
