package com.bonejah.financialapi.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonejah.financialapi.exceptions.AutenticationException;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.repositories.UserRepository;
import com.bonejah.financialapi.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public User authenticate(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		
		if (!user.isPresent()) {
			throw new AutenticationException("User not found!");
		}
		
		if (!user.get().getPassword().equals(password)) {
			throw new AutenticationException("Email/Password invalid!");
		}
			
		return user.get();
	}

	@Transactional
	@Override
	public User save(User user) {
		validEmail(user.getEmail());
		return repository.save(user);
	}

	@Override
	public void validEmail(String email) {
		boolean exists = repository.existsByEmail(email);
		
		if(exists) {
			throw new RuleBusinessException("There is already a registered user with this email.");
		}
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return repository.findById(id);
	}

}
