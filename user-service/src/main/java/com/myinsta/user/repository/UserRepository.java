package com.myinsta.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.myinsta.user.entity.User;

/**
 * @author rima.devwork@gmail.com 
 */
public interface UserRepository extends MongoRepository<User, String>{

	Optional<User> findByUsername(String username);
	
	Optional<User> findByUuid(String uuid);
	
}
