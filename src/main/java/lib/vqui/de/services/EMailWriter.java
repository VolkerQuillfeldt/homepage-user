package lib.vqui.de.services;

import java.io.UnsupportedEncodingException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class EMailWriter {

	@Autowired
	private Constants constants;

	public void sendEmail(String emailLinkHost, Session session, String toEmail, String actionType,
			String actionKey) throws MessagingException, UnsupportedEncodingException {



			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress("no-reply@vquillfeldt.de", "no-reply"));
			msg.setReplyTo(InternetAddress.parse("no-reply@vquillfeldt.de", false));
			msg.setSubject(constants.getMailText().get(actionType + "Subject"), "UTF-8");

			String mailText = constants.getMailText().get(actionType + "Text");
			mailText = mailText.replace("<host>", emailLinkHost );
			mailText = mailText.replace("<actionKey>", actionKey);
			msg.setContent(mailText, "text/html");

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

			Transport.send(msg);


	}
}
