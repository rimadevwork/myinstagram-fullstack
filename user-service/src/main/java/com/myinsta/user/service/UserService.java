package com.myinsta.user.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.myinsta.user.entity.User;
import com.myinsta.user.exception.UserAlreadyExistsException;
import com.myinsta.user.exception.UserNotFoundException;
import com.myinsta.user.repository.UserRepository;
import com.myinsta.user.request.UserProfileUpdateRequest;
import com.myinsta.user.request.UserRegistrationRequest;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the password
		return userRepository.save(user); // Persist in MongoDB
	}

	public void registerUser(UserRegistrationRequest request) {
		if (userRepository.findByUsername(request.getUserName()).isPresent()) {
			logger.debug("Username is already taken: " + request.getUserName());
			throw new UserAlreadyExistsException(request.getUserName());
		}

		User user = new User();
		user.setUsername(request.getUserName());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		//user.setRoles(request.getRoles());
        user.setRoles(List.of("ROLE_USER")); // Hardcode the role 
		saveUser(user); 
	}
	
	public void updateUser(UserProfileUpdateRequest request) {
		//TODO
	}
}