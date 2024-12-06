package com.myinsta.user.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.myinsta.user.entity.User;
import com.myinsta.user.exception.UserAlreadyExistsException;
import com.myinsta.user.exception.UserNotFoundException;
import com.myinsta.user.repository.UserRepository;
import com.myinsta.user.request.LoginRequest;
import com.myinsta.user.request.UserProfileUpdateRequest;
import com.myinsta.user.request.UserRegistrationRequest;

/**
 * @author rima.devwork@gmail.com 
 */
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
	
	public User validateUser(LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUserName());
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
        	return user.get();
        }
        return null;
	}
}
