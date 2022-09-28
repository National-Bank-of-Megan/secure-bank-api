package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.domain.Klik;

@Repository
public interface KlikRepository extends JpaRepository<Klik, String> {
}
