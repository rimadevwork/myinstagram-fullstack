package com.myinsta.user.user_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	public static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);

	public static void main(String[] args) {
		logger.debug("UserServiceApplication starting");
		SpringApplication.run(UserServiceApplication.class, args);
		logger.debug("UserServiceApplication started succesfully");
	}

}
