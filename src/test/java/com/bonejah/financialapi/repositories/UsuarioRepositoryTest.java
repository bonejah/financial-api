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

import com.bonejah.financialapi.models.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		// cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		// acao/execucao
		boolean result = repository.existsByEmail("usuario@email.com.br");

		// verificacao
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalseQuandoNaoHouverUsuarioCadastradoComOEmail() {
		// cenario
//		repository.deleteAll();
		
		// acao/execucao
		boolean result = repository.existsByEmail("usuario@email.com.br");
		
		// verificacao
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		// cenario
		Usuario usuario = criarUsuario();
		
		// acao/execucao
		Usuario usuarioSalvo = repository.save(usuario);
		
		// verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();	 
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		// cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// acao/execucao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com.br");
		
		
		// verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {
		// cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// acao/execucao
		Optional<Usuario> result = repository.findByEmail("usuarioinexistente@email.com.br");
		
		
		// verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("Bruno").email("usuario@email.com.br").senha("12345").build();
	}

}
