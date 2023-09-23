package pl.edu.pw.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.core.domain.FavoriteReceiver;

@Repository
public interface FavoriteReceiverRepository extends JpaRepository<FavoriteReceiver, Long> {

}
