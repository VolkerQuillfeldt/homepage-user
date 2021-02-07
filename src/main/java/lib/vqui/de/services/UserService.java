package lib.vqui.de.services;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import lib.vqui.de.model.dto.ReturnDto;
import lib.vqui.de.model.dto.UserDto;
import lib.vqui.de.model.entitiy.User;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;

import com.lambdaworks.crypto.SCryptUtil;
import org.springframework.stereotype.Service;
import lib.vqui.de.repositories.UserRepository;

@Service
public class UserService {

  private static final String PASSWORD = "password";

  @Autowired
  private EMailService emailService;

  @Autowired
  private UserRepository repository;


  private static final String ERROR_MESSAGE_ACTIVATION = "Account activation failed, please try again later or reset password if already activated !";
  private static final String ERROR_MESSAGE_PWORD = "Account password change failed, please try again later !";
  private static final String MESSAGE_OK = "Account password changed!";


  public ReturnDto registerUser(User user) {
    ReturnDto thisReturn = new ReturnDto();
    try {
      user.setActionType("activate");
      user.setActionKey(SCryptUtil.scrypt(user.getUserRole(), 16, 16, 16));
      User savedUser = repository.saveAndFlush(user);
      thisReturn.setId(savedUser.getId());
      emailService.sendMail(user.getEmail(), user.getActionType(), user.getActionKey());
      thisReturn.setMessage("Account registered, please see your mail to activate your account!");
    } catch (MessagingException | UnsupportedEncodingException messagingException) {
      thisReturn.setId(-10L);
      thisReturn.setMessage("No confirmation mail could be sent, please try again later!");
    } catch (ConstraintViolationException e) {

      if (e.getSQLException().getMessage().contains("user_email_key")) {
        thisReturn.setId(-11L);
        thisReturn.setMessage(
            "Not able to register this user, please try again later or reset password!");
      }

    } catch (HibernateException e) {

      thisReturn.setId(-12L);
      thisReturn.setMessage("Database error, please try again later or reset password!");

    }
    return thisReturn;
  }

  public ReturnDto activateUser(String actionKey) {
    ReturnDto thisReturn = new ReturnDto();

    try {

      List<User> listResult = repository.findByActionKeyAndActionType(actionKey, "activate");

      if (!listResult.isEmpty()) {
        for (User user : listResult) {
          user.setActionKey("");
          user.setActionType("");
          repository.saveAndFlush(user);
          thisReturn.setId(user.getId());
          thisReturn.setMessage("Account activated !");
        }
      } else {
        thisReturn.setId(-11L);
        thisReturn.setMessage(ERROR_MESSAGE_ACTIVATION);
      }

    } catch (HibernateException e) {

      thisReturn.setId(-12L);
      thisReturn.setMessage(ERROR_MESSAGE_ACTIVATION);

    }

    return thisReturn;
  }

  public ReturnDto requestNewPasswordForUser(String email) {
    ReturnDto thisReturn = new ReturnDto();
    thisReturn.setId(0);
    thisReturn.setMessage("An email to reset your password was sent, if an account exists.");
    try {

      List<User> listResult = repository.findByEmail(email);

      if (!listResult.isEmpty()) {
        for (User user : listResult) {
          user.setActionKey(SCryptUtil.scrypt(user.getEmail(), 16, 16, 16));
          user.setActionType(PASSWORD);
          repository.saveAndFlush(user);
          emailService.sendMail(user.getEmail(), user.getActionType(), user.getActionKey());
          thisReturn.setId(user.getId());
        }
      }

    } catch (HibernateException e) {

      thisReturn.setId(-12L);
      thisReturn.setMessage("Account update failed, please try again later !");


    } catch (Exception e) {
      thisReturn.setId(-10L);
      thisReturn.setMessage("No mail could be sent, please try again later!");
    }
    return thisReturn;
  }

  public ReturnDto setNewPasswordForUser(UserDto userDto) {
    ReturnDto thisReturn = new ReturnDto();
    try {

      List<User> listResult = repository
          .findByActionKeyAndActionType(userDto.getActionKey(), PASSWORD);

      if (!listResult.isEmpty()) {
        for (User user : listResult) {
          user.setActionKey("");
          user.setActionType("");
          user.setPassword(SCryptUtil.scrypt(userDto.getPassword(), 16, 16, 16));
          repository.save(user);
          thisReturn.setId(user.getId());
          thisReturn.setMessage(MESSAGE_OK);
        }
      } else {
        thisReturn.setId(-11L);
        thisReturn.setMessage(ERROR_MESSAGE_PWORD);
      }

    } catch (HibernateException e) {

      e.printStackTrace();

      thisReturn.setId(-12L);
      thisReturn.setMessage(ERROR_MESSAGE_PWORD);

    }
    return thisReturn;
  }

  public void checkAdmin() {

    try {

      String email = "admin@vquillfeldt.de";
      List<User> listResult = repository.findByEmail(email);

      if (listResult.isEmpty()) {

        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setDisplayName("Volker Quillfeldt");
        adminUser.setUserRole("admin");
        adminUser.setPassword("*");
        adminUser.setActionKey(SCryptUtil.scrypt(adminUser.getEmail(), 16, 16, 16));
        adminUser.setActionType(PASSWORD);
        repository.saveAndFlush(adminUser);
        emailService
            .sendMail(adminUser.getEmail(), adminUser.getActionType(),
                adminUser.getActionKey());

      }

    } catch (HibernateException hibernateException) {
      hibernateException.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ReturnDto loginUser(UserDto userDto) {

    ReturnDto thisReturn = new ReturnDto();
    thisReturn.setId(-11);
    thisReturn.setMessage("No login into account, please register or change password !");
    try {

      List<User> listResult = repository.findByEmail(userDto.getEmail());

      if (!listResult.isEmpty()) {
        for (User user : listResult) {

          if (SCryptUtil.check(userDto.getPassword(), user.getPassword())) {
            thisReturn.setId(user.getId());
            thisReturn.setAdmin(user.getUserRole().equals("admin"));
            thisReturn.setUserName(user.getDisplayName());
            thisReturn.setMessage("");
          }
        }
      }

    } catch (HibernateException e) {

      thisReturn.setId(-12L);


    }
    return thisReturn;
  }
}
