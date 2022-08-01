package pl.edu.pw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.domain.FavoriteReceiver;

@Repository
public interface FavoriteReceiverRepository extends JpaRepository<FavoriteReceiver, Long> {

}
