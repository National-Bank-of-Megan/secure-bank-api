package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.Otp;

public interface OtpRepository extends JpaRepository<Otp,Long> {
}
