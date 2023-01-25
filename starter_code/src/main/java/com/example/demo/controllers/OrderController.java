package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;
	public static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username, Authentication authentication) {
		User user = userRepository.findByUsername(username);
		if(user == null || !authentication.getName().equals(user.getUsername())) {
			log.error("User " + authentication.getName() + "is not authorized for this action.");
			return ResponseEntity.notFound().build();
		}
		try {
			UserOrder order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);
			log.info("Order (id:" + order.getId() +") is submitted successfully.");
			return ResponseEntity.ok(order);
		} catch (Exception e) {
			log.error("Error when submitting an order: " + e.getMessage());
		}
		return ResponseEntity.ok(null);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username, Authentication authentication) {
		User user = userRepository.findByUsername(username);
		if(user == null || !authentication.getName().equals(user.getUsername())) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
