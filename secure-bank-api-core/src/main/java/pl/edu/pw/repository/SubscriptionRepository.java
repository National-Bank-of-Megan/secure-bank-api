package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.domain.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription,String> {
}
