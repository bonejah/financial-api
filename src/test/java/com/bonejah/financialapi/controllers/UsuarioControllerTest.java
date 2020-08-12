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

import com.bonejah.financialapi.dtos.UsuarioDTO;
import com.bonejah.financialapi.exceptions.ErroAutenticacaoException;
import com.bonejah.financialapi.exceptions.RegraNegocioException;
import com.bonejah.financialapi.models.Usuario;
import com.bonejah.financialapi.services.LancamentoService;
import com.bonejah.financialapi.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

	static final MediaType JSON = MediaType.APPLICATION_JSON;
	static final String API = "/api/usuarios";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		// Cenario
		final String email = "usuario@gmail.com";
		final String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().senha(senha).email(email).build();
		
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execucao and Verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticar"))
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()));		
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		// Cenario
		final String email = "usuario@gmail.com";
		final String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execucao and Verificacao
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
		// Cenario
		final String email = "usuario@gmail.com";
		final String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().senha(senha).email(email).build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execucao and Verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(JSON)
			.contentType(JSON)
			.content(json);
			
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()));		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarSalvarUmUsuarioInvalido() throws Exception {
		// Cenario
		final String email = "usuario@gmail.com";
		final String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Execucao and Verificacao
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
