package com.bonejah.financialapi.repositories;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bonejah.financialapi.models.User;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	UserRepository repository;
	
	@Autowired
	TestEntityManager entityManager;

	@Test
	public void shouldCheckTheExistenceOfAnEmail() {
		// Scene
		User user = createAUser();
		entityManager.persist(user);

		// Execution
		boolean result = repository.existsByEmail("usuario@email.com.br");

		// Verify
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void shouldReturnFalseWhenNotExistUserRegisteredWithEmail() {
		// Scene
//		repository.deleteAll();
		
		// Execution
		boolean result = repository.existsByEmail("usuario@email.com.br");
		
		// Verify
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void shouldPersistAnUser() {
		// Scene
		User user = createAUser();
		
		// Execution
		User userPersisted = repository.save(user);
		
		// Verify
		Assertions.assertThat(userPersisted.getId()).isNotNull();	 
	}
	
	@Test
	public void shouldFindAnUserByEmail() {
		// Scene
		User user = createAUser();
		entityManager.persist(user);
		
		// Execution
		Optional<User> result = repository.findByEmail("usuario@email.com.br");
		
		
		// Verify
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void shouldReturnEmptyWhenFindAUserByEmailAbsent() {
		// Scene
		User user = createAUser();
		entityManager.persist(user);
		
		// Execution
		Optional<User> result = repository.findByEmail("usuarioinexistente@email.com.br");
		
		
		// Verify
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static User createAUser() {
		return User.builder().name("Bruno").email("usuario@email.com.br").password("12345").build();
	}

}
