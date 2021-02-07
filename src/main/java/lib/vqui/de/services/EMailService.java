package lib.vqui.de.services;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
class EMailService {
	
	@Autowired
	private PasswordService pwService;

	@Autowired
	private EMailWriter eMailWriter;
	
	@Value("${email.link.host}")
	String emailLinkHost;
	
	public void sendMail( String toEmail, String actionType, String actionKey)
			throws UnsupportedEncodingException, MessagingException {

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


		eMailWriter.sendEmail(emailLinkHost , session, toEmail, actionType, actionKey);
	}

}
