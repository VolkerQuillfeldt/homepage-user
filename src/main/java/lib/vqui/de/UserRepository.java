package lib.vqui.de;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByEmail(String Email);
	List<User> findByActionKeyAndActionType(String actionKey, String actionType);
}
