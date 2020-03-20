package lib.vqui.de;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan("lib.vqui.de")
public class UserController {

	@Autowired
	private UserDatabaseWorker service;

	@Autowired
	private Constants constants;

		
	@PostMapping("/loginUser")
	public  ReturnJSON loginUser(@RequestBody UserJSON user) {
		return service.loginUser(user);
	}

	@PostMapping("/registerUser")
	public ReturnJSON registerUser(@RequestBody UserJSON user) {
		User dbUser = new User(user);
		return service.registerUser(dbUser , constants);
	}

	@GetMapping("/activateUser")
	public ReturnJSON activateUser(@RequestParam String actionKey) {
		return service.activateUser(actionKey);
	}
	
	@PostMapping("/requestNewPasswordforUser")
	public ReturnJSON requestNewPasswordforUser(@RequestBody UserJSON user) {
		return service.requestNewPasswordforUser(user.getEmail(), constants);
	}
	
	@PostMapping("/setnewPasswordforUser")
	public ReturnJSON setnewPasswordforUser(@RequestBody UserJSON user) {
		return service.setNewPasswordForUser(user);
	}
	
	@PostConstruct
	public void init() {
		service.checkAdmin(constants);
	}

}
