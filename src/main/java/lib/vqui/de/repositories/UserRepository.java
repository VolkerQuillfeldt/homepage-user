package lib.vqui.de.repositories;

import java.util.List;

import lib.vqui.de.model.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByEmail(String eMail);
	List<User> findByActionKeyAndActionType(String actionKey, String actionType);
}
