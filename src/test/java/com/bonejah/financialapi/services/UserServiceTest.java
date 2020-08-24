package com.bonejah.financialapi.services;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bonejah.financialapi.exceptions.AutenticationException;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.repositories.UserRepository;
import com.bonejah.financialapi.services.impl.UserServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

//	@Autowired
//	UsuarioService service;

	@SpyBean
	UserServiceImpl service;

	@MockBean
	UserRepository repository;

	@BeforeEach
	public void setup() {
//		service = new UsuarioServiceImpl(repository);
//		service = Mockito.spy(UsuarioServiceImpl.class);
	}

	@Test
	public void shouldValidEmail() {
		// Scene
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// Execution
		service.validEmail("123@gmail.com");

		// Verify
	}

	@Test()
	public void shouldThrowErroWhenValidInexistEmail() {
		// Scene
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// Execution
		try {
			service.validEmail("usuario@gmail.com");
		} catch (RuleBusinessException e) {
			// Verify
			Assertions.assertThat(e.getMessage()).contains("There is already a registered user with this email.");
		}
	}

	@Test
	public void shouldAuthenticateAUserWithSuccess() {
		// Scene
		String email = "email@gmail.com";
		String password = "senha";

		User user = User.builder().email(email).password(password).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(user));

		// acao
		User result = service.authenticate(email, password);

		// Verify
		Assertions.assertThat(result).isNotNull();
	}

	@Test
	public void shouldThrowErrorWhenNotFoundUserWithEmailRegistered() {
		// Scene
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// acao
		try {
			service.authenticate("usuario@gmail.com", "senha");
		} catch (AutenticationException e) {
			// Verify
			Assertions.assertThat(e.getMessage()).contains("User not found.");
		}
	}

	@Test
	public void shouldThrowErroWhenPasswordInvalid() {
		// Scene
		String password = "senha";
		User user = User.builder().email("email@email.com").password(password).id(1L).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));

		// acao
		Throwable exception = Assertions.catchThrowable(() -> service.authenticate("email@email.com", "123"));

		// Verify
		Assertions.assertThat(exception).isInstanceOf(AutenticationException.class)
				.hasMessage("Email/Password invalid!");
	}

	@Test
	public void shouldSaveAUser() {
		// Scene
		Mockito.doNothing().when(service).validEmail(Mockito.anyString());
		User user = User.builder().email("email@email.com").password("123").id(1L).name("nome").build();
		Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);

		// acao
		User userCreated = service.save(new User());

		// Verify
		Assertions.assertThat(userCreated).isNotNull();
		Assertions.assertThat(userCreated.getId()).isEqualTo(1L);
		Assertions.assertThat(userCreated.getName()).isEqualTo("nome");
		Assertions.assertThat(userCreated.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(userCreated.getPassword()).isEqualTo("123");
	}

	@Test
	public void shouldNotSaveAUserWhenEmailAlreadyRegister() {
		// Scene
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// acao
		org.junit.jupiter.api.Assertions.assertThrows(RuleBusinessException.class,
				() -> service.validEmail("email@email.com"));
	}

}
