package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.domain.Otp;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByClientId(Long clientId);
    Optional<Otp> findByOtp(String otp);
}
