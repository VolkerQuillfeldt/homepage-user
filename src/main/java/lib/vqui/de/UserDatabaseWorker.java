package lib.vqui.de;

import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.lambdaworks.crypto.SCryptUtil;

@ComponentScan("lib.vqui.de")
@Component("UserDatabaseWorker")
public class UserDatabaseWorker {

	@Autowired
	private EMailService emailService;

	private static final String ERRORMESSAGEACTIVATION = "Account activation failed, please try again later or reset password if already activated !";
	private static final String ERRORMESSAGEPWORD = "Account password change failed, please try again later !";
	private static final String MESSAGEOK = "Account password changed!";
	@Value("${hibernate.config}")
	String hibernateConfig;

	protected StandardServiceRegistry registry = null;

	protected SessionFactory setup() {
		
		SessionFactory sessionFactory = null;

		if (registry == null) {
			registry = new StandardServiceRegistryBuilder().configure(hibernateConfig) // configures
					// from
					// hibernate.cfg.xml
					.build();
		}
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			registry = null;
		}

		return sessionFactory;
	}

	protected ReturnJSON registerUser(User user, Constants constants) {
		ReturnJSON thisReturn = new ReturnJSON();
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();
				user.actionType = "activate";
				user.actionKey = SCryptUtil.scrypt(user.email, 16, 16, 16);
				thisReturn.setId((long) session.save(user));
				try {
					emailService.sendMail(constants, user.email, user.actionType, user.actionKey);
					thisReturn.setMessage("Account registered, please see your mail to activate your account!");
				} catch (Exception e) {
					session.getTransaction().rollback();
					thisReturn.setId(-10l);
					thisReturn.setMessage("No confirmation mail could be sent, please try again later!");
				}
			}
		} catch (ConstraintViolationException e) {

			if (e.getSQLException().getMessage().contains("user_email_key")) {
				thisReturn.setId(-11l);
				thisReturn.setMessage("Not able to register this user, please try again later or reset password!");
			}

		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage("Database error, please try again later or reset password!");

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}
		return thisReturn;
	}

	protected ReturnJSON activateUser(String actionKey) {
		ReturnJSON thisReturn = new ReturnJSON();
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();

				String hql = "SELECT u from User u where u.actionKey='" + actionKey + "'";
				Query<User> query = session.createQuery(hql);

				List<User> listResult = query.list();

				if (!listResult.isEmpty()) {
					for (User user : listResult) {
						Transaction tx = session.beginTransaction();
						user.setActionKey("");
						user.setActionType("");
						session.update(user);
						tx.commit();
						thisReturn.setId(user.getId());
						thisReturn.setMessage("Account acctivated !");
					}
				} else {

					thisReturn.setId(-11l);
					thisReturn.setMessage(ERRORMESSAGEACTIVATION);
				}
			}
		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage(ERRORMESSAGEACTIVATION);

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}

		return thisReturn;
	}

	protected ReturnJSON requestNewPasswordforUser(String email, Constants constants) {

		ReturnJSON thisReturn = new ReturnJSON();
		thisReturn.setId(0);
		thisReturn.setMessage("An email to reset your password was sent, if an account excists.");
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();

				String hql = "SELECT u from User u where u.email='" + email + "'";
				Query<User> query = session.createQuery(hql);

				List<User> listResult = query.list();

				if (!listResult.isEmpty()) {
					for (User user : listResult) {
						Transaction tx = session.beginTransaction();
						user.setActionKey(SCryptUtil.scrypt(user.email, 16, 16, 16));
						user.setActionType("password");
						session.update(user);
						tx.commit();

						try {
							emailService.sendMail(constants, user.email, user.actionType, user.actionKey);
						} catch (Exception e) {
							session.getTransaction().rollback();
							thisReturn.setId(-10l);
							thisReturn.setMessage("No mail could be sent, please try again later!");
						}
						thisReturn.setId(user.getId());
					}
				}
			}
		} catch (HibernateException e) {

			thisReturn.setId(-12l);
			thisReturn.setMessage("Account update failed, please try again later !");

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}
		return thisReturn;
	}

	protected ReturnJSON setNewPasswordForUser(UserJSON userJSON) {
		ReturnJSON thisReturn = new ReturnJSON();
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();

				String hql = "SELECT u from User u where u.actionKey='" + userJSON.getActionKey() + "'";
				Query<User> query = session.createQuery(hql);

				List<User> listResult = query.list();

				if (!listResult.isEmpty()) {
					for (User user : listResult) {
						Transaction tx = session.beginTransaction();
						user.setActionKey("");
						user.setActionType("");
						user.setPassword(SCryptUtil.scrypt(userJSON.password, 16, 16, 16));
						session.update(user);
						tx.commit();
						thisReturn.setId(user.getId());
						thisReturn.setMessage(MESSAGEOK);
					}
				} else {
					thisReturn.setId(-11l);
					thisReturn.setMessage(ERRORMESSAGEPWORD);
				}
			}
		} catch (HibernateException e) {

			e.printStackTrace();

			thisReturn.setId(-12l);
			thisReturn.setMessage(ERRORMESSAGEPWORD);

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}
		return thisReturn;
	}

	@PostConstruct
	public void init() {
		this.setup();

	}

	protected void checkAdmin(Constants constants) {
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();

				String email = "admin@vquillfeldt.de";
				String hql = "SELECT u from User u where u.email='" + email + "'";

				Query<User> query = session.createQuery(hql);

				List<User> listResult = query.list();

				if (listResult.isEmpty()) {

					User adminUser = new User();
					adminUser.setEmail(email);
					adminUser.setDisplayName("Volker Quillfeldt");
					adminUser.setUserRole("admin");
					adminUser.setPassword("*");
					adminUser.setActionKey(SCryptUtil.scrypt(adminUser.email, 16, 16, 16));
					adminUser.setActionType("password");
					session.save(adminUser);

					try {
						emailService.sendMail(constants, adminUser.email, adminUser.actionType, adminUser.actionKey);
					} catch (Exception e) {
						session.getTransaction().rollback();
						e.printStackTrace();
					}

				}
			}
		} catch (HibernateException e) {

			e.printStackTrace();

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}
	}

	protected ReturnJSON loginUser(UserJSON userJSON) {

		ReturnJSON thisReturn = new ReturnJSON();
		thisReturn.setId(-11);
		thisReturn.setMessage("No login into account, please register or change password !");
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = setup();
			if (sessionFactory.isOpen()) {
				session = sessionFactory.openSession();

				String hql = "SELECT u from User u where u.email='" + userJSON.getEmail() + "'";
				Query<User> query = session.createQuery(hql);

				List<User> listResult = query.list();

				if (!listResult.isEmpty()) {
					for (User user : listResult) {

						if (SCryptUtil.check(userJSON.getPassword(), user.getPassword())) {
							thisReturn.setId(user.getId());
							thisReturn.setAdmin(user.getUserRole().equals("admin"));
							thisReturn.setUserName(user.getDisplayName());
							thisReturn.setMessage("");
						}
					}
				}
			}
		} catch (HibernateException e) {

			thisReturn.setId(-12l);

		} finally {
			if (session != null)
				session.close();
			if (sessionFactory != null)
				sessionFactory.close();

		}
		return thisReturn;
	}
}
