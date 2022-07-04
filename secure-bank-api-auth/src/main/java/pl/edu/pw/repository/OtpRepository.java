package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.Otp;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByClientId(Long clientId);
}
