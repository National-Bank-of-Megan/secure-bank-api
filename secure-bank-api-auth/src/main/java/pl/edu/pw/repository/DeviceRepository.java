package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByIp(String ip);

    List<Device> findAllByAccountClientId(String clientId);

    Optional<Device> findByFingerprintAndAccountClientId(String fingerprint, String clientId);

    Optional<Device> findByFingerprint(String fingerprint);
}
