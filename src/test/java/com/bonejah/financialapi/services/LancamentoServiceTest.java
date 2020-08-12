package com.bonejah.financialapi.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bonejah.financialapi.enums.StatusLancamento;
import com.bonejah.financialapi.exceptions.RegraNegocioException;
import com.bonejah.financialapi.models.Lancamento;
import com.bonejah.financialapi.models.Usuario;
import com.bonejah.financialapi.repositories.LancamentoRepository;
import com.bonejah.financialapi.repositories.LancamentoRepositoryTest;
import com.bonejah.financialapi.services.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		// Cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// Execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		// Verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// Execucao e Verificacao
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// Cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// Execucao
		service.atualizar(lancamentoSalvo);

		// Verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizar() {
		// Cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// Execucao e Verificacao
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// Cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		// Execucao
		service.deletar(lancamento);

		// Verificacao
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// Execucao
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// Verificacao
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void deveFiltrarLancamentos() {
		// Cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Execucao
		List<Lancamento> resultado = service.buscar(lancamento);

		// Verificacoes
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// Excecucao
		service.atualizarStatus(lancamento, novoStatus);

		// Verificacao
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// Cenario
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Execucao
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoLancamentoNaoExiste() {
		// Cenario
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execucao
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErroAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descricao válida.");

		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descricao válida.");
		
		lancamento.setDescricao("Descricao");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe Usuário.");
		
		lancamento.setUsuario(new Usuario());

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe Usuário.");
		
		lancamento.getUsuario().setId(1L);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(10L));
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lancamento.");
	}

}
