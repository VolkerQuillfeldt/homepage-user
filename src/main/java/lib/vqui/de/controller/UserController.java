package lib.vqui.de.controller;

import javax.annotation.PostConstruct;

import lib.vqui.de.model.dto.ReturnDto;
import lib.vqui.de.model.dto.UserDto;
import lib.vqui.de.model.entitiy.User;
import lib.vqui.de.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService service;

	@PostMapping("/loginUser")
	public ReturnDto loginUser(@RequestBody UserDto user) {
		return service.loginUser(user);
	}

	@PostMapping("/registerUser")
	public ReturnDto registerUser(@RequestBody UserDto user) {
		User dbUser = new User(user);
		return service.registerUser(dbUser);
	}

	@GetMapping("/activateUser")
	public ReturnDto activateUser(@RequestParam String actionKey) {
		return service.activateUser(actionKey);
	}

	@PostMapping("/requestNewPasswordForUser")
	public ReturnDto requestNewPasswordForUser(@RequestBody UserDto user) {
		return service.requestNewPasswordForUser(user.getEmail());
	}

	@PostMapping("/setNewPasswordForUser")
	public ReturnDto setNewPasswordForUser(@RequestBody UserDto user) {
		return service.setNewPasswordForUser(user);
	}

	@PostConstruct
	public void init() {
		service.checkAdmin();
	}

}
