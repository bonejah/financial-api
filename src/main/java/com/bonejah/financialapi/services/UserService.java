package com.bonejah.financialapi.services;

import java.util.Optional;

import com.bonejah.financialapi.models.User;

public interface UserService {	
	
	User authenticate(String email, String password);
	
	User save(User user);
	
	void validEmail(String email);
	
	Optional<User> getUserById(Long id);
	
}
