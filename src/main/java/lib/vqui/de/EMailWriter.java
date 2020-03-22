package lib.vqui.de;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EMailWriter {

	private EMailWriter() {

	}

	public static void sendEmail(String emailLinkHost, Constants constants, Session session, String toEmail, String actionType,
			String actionKey) {

		try {

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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
