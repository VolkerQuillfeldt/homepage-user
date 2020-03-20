package lib.vqui.de;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.lambdaworks.crypto.SCryptUtil;

@Entity
@Table(name = "us.user")
public class User {

	public User() {
		
	}
	
	public User(UserJSON user) {
		this.email = user.email;
		this.password = SCryptUtil.scrypt(user.password , 16, 16, 16);
		this.userRole = user.userRole;
		this.displayName= user.displayName;
		this.actionKey = user.actionKey;
		this.actionType = user.actionType;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getActionKey() {
		return actionKey;
	}

	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		 this.actionType = actionType;
	}
	
}
