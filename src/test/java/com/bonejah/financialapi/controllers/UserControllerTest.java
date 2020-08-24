package com.bonejah.financialapi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bonejah.financialapi.dtos.UserDTO;
import com.bonejah.financialapi.exceptions.AutenticationException;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.services.BalanceService;
import com.bonejah.financialapi.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	static final MediaType JSON = MediaType.APPLICATION_JSON;
	static final String API = "/api/users";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UserService service;
	
	@MockBean
	BalanceService lancamentoService;
	
	@Test
	public void shouldAuthenticateUser() throws Exception {
		// Scene
		final String email = "usuario@gmail.com";
		final String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		User user = User.builder().password(password).email(email).build();
		
		Mockito.when(service.authenticate(email, password)).thenReturn(user);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execute and Verify
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticar"))
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()));		
	}
	
	@Test
	public void shouldReturnBadRequestWhenHappensAuthenticationError() throws Exception {
		// Scene
		final String email = "usuario@gmail.com";
		final String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		
		Mockito.when(service.authenticate(email, password)).thenThrow(AutenticationException.class);		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execute and Verify
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticar"))
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());		
	}
	
	@Test
	public void deveSalvarUmUsuario() throws Exception {
		// Scene
		final String email = "usuario@gmail.com";
		final String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		User user = User.builder().password(password).email(email).build();
		
		Mockito.when(service.save(Mockito.any(User.class))).thenReturn(user);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execute and Verify
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(user.getName()));		
	}
	
	@Test
	public void shouldReturnBadRequestWhenTryToSaveAnInvalidUser() throws Exception {
		// Scene
		final String email = "usuario@gmail.com";
		final String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		
		Mockito.when(service.save(Mockito.any(User.class))).thenThrow(RuleBusinessException.class);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execute and Verify
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());		
	}

}
