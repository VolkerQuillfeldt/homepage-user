package lib.vqui.de;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Base64;

@SuppressWarnings("restriction")
@Component("PasswordService")
@Service
public class PasswordService {

	@Value("${email.pw}")
	String passwordEncrypt;
	
	public PasswordService() {
		/*
		 * Constructor
		 */
	}
	
	public String getEMailPassword() {
		return decrypt(passwordEncrypt);
	}
	
	
	private static String decrypt(String encstr) {

		if (encstr.length() > 12) {

			String cipher = encstr.substring(12);
			return new String(Base64.getDecoder().decode(cipher));
		}

		return null;
	}
}
