package lib.vqui.de;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan("lib.vqui.de")
@Component("EMailService")
public class EMailService {
	
	@Autowired
	private PasswordService pwService;
	
	@Value("${email.link.host}")
	String emailLinkHost;
	
	public void sendMail(Constants constants, String toEmail, String actionType, String actionKey) {

		final String fromEmail = "no-reply@vquillfeldt.de"; // requires valid gmail id
		final String password =  pwService.getEMailPassword() ; // correct password for gmail id
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.ionos.de"); // SMTP Host
		props.put("mail.smtp.socketFactory.port", "587"); // SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
		props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		props.put("mail.smtp.port", "587"); // SMTP Port
		props.put("mail.smtp.ssl.checkserveridentity", true); // Compliant


		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		Session session = Session.getDefaultInstance(props, auth);
		
		

		EMailWriter.sendEmail(emailLinkHost , constants, session, toEmail, actionType, actionKey);
	}

}
