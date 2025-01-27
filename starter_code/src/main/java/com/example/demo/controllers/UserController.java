package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;
	public static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())
				|| createUserRequest.getPassword().length() < 8) {
			log.error("Password does not meet the safety requirement.");
			return ResponseEntity.badRequest().build();
		}
		User user = new User();
		try {
			user.setUsername(createUserRequest.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
		} catch (Exception e) {
			log.error("Create new user Error: " + e.getMessage());
		}
		log.info("New user " + user.getUsername() + " created successfully.");
		return ResponseEntity.ok(user);
	}
	
}
