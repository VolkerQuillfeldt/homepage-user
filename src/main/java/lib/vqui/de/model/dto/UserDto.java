package lib.vqui.de.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
	
	String email;
	String password;
	String userRole;
	String displayName;
	String actionKey;
	String actionType;

}
