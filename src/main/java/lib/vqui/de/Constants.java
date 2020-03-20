package lib.vqui.de;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component("Constants")
public class Constants {

	protected HashMap<String, String> mailText = new HashMap<>();
	protected static String htmlBR = "<br />";
	

	private void initialize() {

		mailText.put("activateSubject", "Please activate your account at vquillfeldt.de");

		StringBuilder messageHTML = new StringBuilder();
		messageHTML.append("<h1>Thank you for your registration at vquillfeldt.de</h1>");
		messageHTML.append( htmlBR);
		messageHTML.append(htmlBR);
		messageHTML.append("Please click the following link to activate your account");
		messageHTML.append(htmlBR);
		messageHTML.append("<a href='http://<host>/openActivate?actionKey=<actionKey>'>activate account</a>");
		messageHTML.append(htmlBR);

		mailText.put("activateText", messageHTML.toString());

		mailText.put("passwordSubject", "Please change your password for your account at vquillfeldt.de");
		messageHTML = new StringBuilder();
		messageHTML.append("<h1>Thank you for your contacting vquillfeldt.de</h1>");
		messageHTML.append(htmlBR);
		messageHTML.append(htmlBR);
		messageHTML.append("Please click the following link to change your password for your account");
		messageHTML.append(htmlBR);
		messageHTML.append("<a href='http://<host>/openChangePassword?actionKey=<actionKey>'>change password</a>");
		messageHTML.append(htmlBR);
		mailText.put("passwordText", messageHTML.toString());
	}

	public Map<String, String> getMailText() {
		return mailText;
	}
	
	@PostConstruct
	public void init() {
		this.initialize();
	}

}
