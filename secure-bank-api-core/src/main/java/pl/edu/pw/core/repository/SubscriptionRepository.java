package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.core.domain.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
}
