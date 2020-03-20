package lib.vqui.de;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;

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

			BASE64Decoder decoder = new BASE64Decoder();

			try {

				return new String(decoder.decodeBuffer(cipher));

			} catch (IOException e) {

				// throw new InvalidImplementationException(

				// Fail

			}

		}

		return null;
	}
}
