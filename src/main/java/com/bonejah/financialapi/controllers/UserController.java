package com.bonejah.financialapi.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonejah.financialapi.dtos.UserDTO;
import com.bonejah.financialapi.exceptions.AutenticationException;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.services.BalanceService;
import com.bonejah.financialapi.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private UserService userService;
	private BalanceService balanceService;
	
	public UserController(UserService userService, BalanceService balanceService) {
		this.userService = userService;
		this.balanceService = balanceService;
	}
	
	@PostMapping
	public ResponseEntity<?> save(@RequestBody UserDTO dto) {
		User user = User.builder()
							.email(dto.getEmail())
							.name(dto.getName())
							.password(dto.getPassword()).build();
		try {
			User userSaved = userService.save(user);
			return new ResponseEntity<User>(userSaved, HttpStatus.CREATED);
		} catch (RuleBusinessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO dto) {
		try {
			User userAuthenticated = userService.authenticate(dto.getEmail(), dto.getPassword());
			return ResponseEntity.ok(userAuthenticated);
		} catch (AutenticationException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/balance")
	public ResponseEntity<?> getBalanceByIdUser(@PathVariable("id") Long id) {
		Optional<User> userFounded = userService.getUserById(id);
		
		if (!userFounded.isPresent()) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal balance = balanceService.getBalanceByIdUser(id);
		return ResponseEntity.ok(balance);
	}
	
	
	@GetMapping("/")
	public String helloWorld() {
		return "Hello World";
	}
}
