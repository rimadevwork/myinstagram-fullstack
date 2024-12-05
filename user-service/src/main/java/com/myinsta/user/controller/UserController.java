package com.myinsta.user.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myinsta.user.entity.User;
import com.myinsta.user.request.LoginRequest;
import com.myinsta.user.request.UserProfileUpdateRequest;
import com.myinsta.user.request.UserRegistrationRequest;
import com.myinsta.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/hello")
	public ResponseEntity<String> sayHello() {
		logger.debug("Received hello request");
		return ResponseEntity.status(HttpStatus.OK).body("Hello, the server is up and running!");
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
		logger.debug("Received registration request for username: {}", request.getUserName());
		userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/updateProfile")
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest request) {
		logger.debug("Received registration request for username: {}", request.getUserName());
		userService.updateUser(request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

	@PostMapping("/validateUser")
	public ResponseEntity<?> validateUser(@RequestBody LoginRequest request) {
		logger.debug("Validating Login request for username: " + request.getUserName() + " and pwd: "
				+ request.getPassword());
		User user = userService.validateUser(request);
		if (user != null) {
			return ResponseEntity.ok(Map.of("status", "success", "userId", user.getUuid(), "roles", user.getRoles()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("status", "failure", "message", "Invalid username or password"));
	}
}
