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

import com.bonejah.financialapi.exceptions.ErroAutenticacaoException;
import com.bonejah.financialapi.exceptions.RegraNegocioException;
import com.bonejah.financialapi.models.Usuario;
import com.bonejah.financialapi.repositories.UsuarioRepository;
import com.bonejah.financialapi.services.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {

//	@Autowired
//	UsuarioService service;

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@BeforeEach
	public void setup() {
//		service = new UsuarioServiceImpl(repository);
//		service = Mockito.spy(UsuarioServiceImpl.class);
	}

	@Test
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// acao/execucao
		service.validarEmail("123@gmail.com");

		// verificacao
	}

	@Test()
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// acao/execucao
		try {
			service.validarEmail("usuario@gmail.com");
		} catch (RegraNegocioException e) {
			// verificacao
			Assertions.assertThat(e.getMessage()).contains("Já existe um usuário cadsatrado com este email.");
		}
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		// cenario
		String email = "email@gmail.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// acao
		Usuario result = service.autenticar(email, senha);

		// verificacao
		Assertions.assertThat(result).isNotNull();
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		// cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// acao
		try {
			service.autenticar("usuario@gmail.com", "senha");
		} catch (ErroAutenticacaoException e) {
			// verificacao
			Assertions.assertThat(e.getMessage()).contains("Usuário não encontrado.");
		}
	}

	@Test
	public void deveLancarErroQuandoASenhaForInvalida() {
		// cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// acao
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));

		// verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class)
				.hasMessage("Usuário/Inválido não encontrado.");
	}

	@Test
	public void deveSalvarUmUsuario() {
		// cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().email("email@email.com").senha("123").id(1L).nome("nome").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		// acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

		// verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("123");
	}

	@Test
	public void naoDeveSalvarUmUsuarioComOEmailJaCadastrado() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// acao
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class,
				() -> service.validarEmail("email@email.com"));
	}

}
