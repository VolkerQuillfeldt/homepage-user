package lib.vqui.de;

import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.lambdaworks.crypto.SCryptUtil;

@ComponentScan("lib.vqui.de")
@Component("UserDatabaseWorker")
public class UserDatabaseWorker {
	
	@Autowired
	private EMailService emailService;
	
	@Autowired
	UserRepository repository;


	private static final String ERRORMESSAGEACTIVATION = "Account activation failed, please try again later or reset password if already activated !";
	private static final String ERRORMESSAGEPWORD = "Account password change failed, please try again later !";
	private static final String MESSAGEOK = "Account password changed!";
	
	
	protected ReturnJSON registerUser(User user, Constants constants) {
		ReturnJSON thisReturn = new ReturnJSON();
		try {
			user.actionType = "activate";
			user.actionKey = SCryptUtil.scrypt(user.email, 16, 16, 16);
			User savedUser = repository.saveAndFlush(user);
			thisReturn.setId((long) savedUser.getId());
			try {
				emailService.sendMail(constants, user.email, user.actionType, user.actionKey);
				thisReturn.setMessage("Account registered, please see your mail to activate your account!");
			} catch (Exception e) {
				
				thisReturn.setId(-10l);
				thisReturn.setMessage("No confirmation mail could be sent, please try again later!");
			}

		} catch (ConstraintViolationException e) {

			if (e.getSQLException().getMessage().contains("user_email_key")) {
				thisReturn.setId(-11l);
				thisReturn.setMessage("Not able to register this user, please try again later or reset password!");
			}

		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage("Database error, please try again later or reset password!");

		} 
		return thisReturn;
	}

	public ReturnJSON activateUser(String actionKey) {
		ReturnJSON thisReturn = new ReturnJSON();
	
		try {
			
			List<User> listResult = repository.findByActionKeyAndActionType(actionKey,"activate");

			if (!listResult.isEmpty()) {
				for (User user : listResult) {
					user.setActionKey("");
					user.setActionType("");
					repository.saveAndFlush(user);
					thisReturn.setId(user.getId());
					thisReturn.setMessage("Account acctivated !");
				}
			} else {

				thisReturn.setId(-11l);
				thisReturn.setMessage(ERRORMESSAGEACTIVATION);
			}

		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage(ERRORMESSAGEACTIVATION);

		}

		return thisReturn;
	}

	public ReturnJSON requestNewPasswordforUser(String email, Constants constants) {
		ReturnJSON thisReturn = new ReturnJSON();
		thisReturn.setId(0);
		thisReturn.setMessage("An email to reset your password was sent, if an account excists.");
		try {
			
			List<User> listResult = repository.findByEmail(email);

			if (!listResult.isEmpty()) {
				for (User user : listResult) {
					user.setActionKey(SCryptUtil.scrypt(user.email, 16, 16, 16));
					user.setActionType("password");
					repository.saveAndFlush(user);
					try {
						emailService.sendMail(constants, user.email, user.actionType, user.actionKey);
					} catch (Exception e) {
						thisReturn.setId(-10l);
						thisReturn.setMessage("No mail could be sent, please try again later!");
					}
					thisReturn.setId(user.getId());
				}
			}

		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage("Account update failed, please try again later !");

		} 
		return thisReturn;
	}

	public ReturnJSON setNewPasswordForUser(UserJSON userJSON) {
		ReturnJSON thisReturn = new ReturnJSON();
		try {
			
			List<User> listResult = repository.findByActionKeyAndActionType(userJSON.getActionKey(),"password");

			if (!listResult.isEmpty()) {
				for (User user : listResult) {
					user.setActionKey("");
					user.setActionType("");
					user.setPassword(SCryptUtil.scrypt(userJSON.password, 16, 16, 16));
					repository.save(user);
					thisReturn.setId(user.getId());
					thisReturn.setMessage(MESSAGEOK);
				}
			} else {
				thisReturn.setId(-11l);
				thisReturn.setMessage(ERRORMESSAGEPWORD);
			}

		} catch (HibernateException e) {

			e.printStackTrace();
			
			thisReturn.setId(-12l);
			thisReturn.setMessage(ERRORMESSAGEPWORD);

		} 
		return thisReturn;
	}

	protected void exit() {
	
	}

	@PostConstruct
	public void init() {
	
	}

	public void checkAdmin(Constants constants) {

		try {
			
			String email = "admin@vquillfeldt.de";
			List<User> listResult = repository.findByEmail(email);
			
			if (listResult.isEmpty()) {

				User adminUser = new User();
				adminUser.setEmail(email);
				adminUser.setDisplayName("Volker Quillfeldt");
				adminUser.setUserRole("admin");
				adminUser.setPassword("*");
				adminUser.setActionKey(SCryptUtil.scrypt(adminUser.email, 16, 16, 16));
				adminUser.setActionType("password");
				repository.saveAndFlush(adminUser);
				try {
					emailService.sendMail(constants, adminUser.email, adminUser.actionType, adminUser.actionKey);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (HibernateException e) {
			e.printStackTrace();
		} 
	}

	public ReturnJSON loginUser(UserJSON userJSON) {
		
		ReturnJSON thisReturn = new ReturnJSON();
		thisReturn.setId(-11);
		thisReturn.setMessage("No login into account, please register or change password !");
		try {
			
			List<User> listResult = repository.findByEmail(userJSON.getEmail());
			
			if (!listResult.isEmpty()) {
				for (User user : listResult) {
					
					if(SCryptUtil.check(userJSON.getPassword(), user.getPassword())){
						thisReturn.setId(user.getId());
						thisReturn.setAdmin(user.getUserRole().equals("admin"));
						thisReturn.setUserName(user.getDisplayName());
						thisReturn.setMessage("");					}
				}
			}

		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			

		} 
		return thisReturn;
	}
}
