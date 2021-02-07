package lib.vqui.de.model.entitiy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.lambdaworks.crypto.SCryptUtil;
import lib.vqui.de.model.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user", schema="us")
@Getter
@Setter
@NoArgsConstructor
public class User {

	public User(UserDto user) {
		this.email = user.getEmail();
		this.password = SCryptUtil.scrypt(user.getPassword() , 16, 16, 16);
		this.userRole = user.getUserRole();
		this.displayName= user.getDisplayName();
		this.actionKey = user.getActionKey();
		this.actionType = user.getActionType();
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(name = "email")
	String email;
	@Column(name = "password")
	String password;
	@Column(name = "user_role")
	String userRole;
	@Column(name = "display_name")
	String displayName;
	@Column(name = "action_key")
	String actionKey;
	@Column(name = "action_type")
	String actionType;

}
