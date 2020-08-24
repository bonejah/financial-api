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

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.Balance;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.repositories.BalanceRepository;
import com.bonejah.financialapi.repositories.BalanceRepositoryTest;
import com.bonejah.financialapi.services.impl.BalanceServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class BalanceServiceTest {

	@SpyBean
	BalanceServiceImpl service;

	@MockBean
	BalanceRepository repository;

	@Test
	public void shouldSaveABalance() {
		// Scene
		Balance balanceToSave = BalanceRepositoryTest.createBalance();

		Mockito.doNothing().when(service).validBalance(balanceToSave);

		Balance balanceSaved = BalanceRepositoryTest.createBalance();
		balanceSaved.setId(1L);
		balanceSaved.setStatus(StatusBalance.PENDING);

		Mockito.when(repository.save(balanceToSave)).thenReturn(balanceSaved);

		// Execution
		Balance balance = service.save(balanceToSave);

		// Verify
		Assertions.assertThat(balance.getId()).isEqualTo(balanceSaved.getId());
		Assertions.assertThat(balance.getStatus()).isEqualTo(StatusBalance.PENDING);
	}

	@Test
	public void shouldntSaveABalanceWhenOccurErrorOnValidate() {
		// Scene
		Balance balanceToSave = BalanceRepositoryTest.createBalance();

		// Execution and Verify
		Mockito.doThrow(RuleBusinessException.class).when(service).validBalance(balanceToSave);

		Assertions.catchThrowableOfType(() -> service.save(balanceToSave), RuleBusinessException.class);

		Mockito.verify(repository, Mockito.never()).save(balanceToSave);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// Scene
		Balance lancamentoSalvo = BalanceRepositoryTest.createBalance();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusBalance.PENDING);

		Mockito.doNothing().when(service).validBalance(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// Execution
		service.update(lancamentoSalvo);

		// Verify
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizar() {
		// Scene
		Balance lancamentoASalvar = BalanceRepositoryTest.createBalance();

		// Execution e Verify
		Assertions.catchThrowableOfType(() -> service.update(lancamentoASalvar), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void shouldDeleteABalance() {
		// Scene
		Balance balance = BalanceRepositoryTest.createBalance();
		balance.setId(1L);

		// Execution
		service.delete(balance);

		// Verify
		Mockito.verify(repository).delete(balance);
	}

	@Test
	public void shouldThrowErroWhenTryDeleteABalanceWhenThatNotSaved() {
		// Scene
		Balance balance = BalanceRepositoryTest.createBalance();

		// Execution
		Assertions.catchThrowableOfType(() -> service.delete(balance), NullPointerException.class);

		// Verify
		Mockito.verify(repository, Mockito.never()).delete(balance);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldFilterABalance() {
		// Scene
		Balance balance = BalanceRepositoryTest.createBalance();
		balance.setId(1L);

		List<Balance> listBalance = Arrays.asList(balance);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(listBalance);

		// Execution
		List<Balance> result = service.getListBalance(balance);

		// Verificacoes
		Assertions.assertThat(result).isNotEmpty().hasSize(1).contains(balance);
	}

	@Test
	public void shouldUpdateABalanceStatus() {
		// Scene
		Balance balance = BalanceRepositoryTest.createBalance();
		balance.setId(1L);
		balance.setStatus(StatusBalance.PENDING);

		StatusBalance newStatus = StatusBalance.EFFECTIVE;
		Mockito.doReturn(balance).when(service).update(balance);

		// Excecution
		service.updateStatus(balance, newStatus);

		// Verify
		Assertions.assertThat(balance.getStatus()).isEqualTo(newStatus);
		Mockito.verify(service).update(balance);
	}

	@Test
	public void shouldGetBalanceById() {
		// Scene
		Long id = 1L;

		Balance balance = BalanceRepositoryTest.createBalance();
		balance.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(balance));

		// Execution
		Optional<Balance> result = service.getBalanceById(id);

		// Verify
		Assertions.assertThat(result.isPresent()).isTrue();
	}

	@Test
	public void shouldReturnEmptyWhenBalanceNotExists() {
		// Scene
		Long id = 1L;

		Balance balance = BalanceRepositoryTest.createBalance();
		balance.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execution
		Optional<Balance> resultado = service.getBalanceById(id);

		// Verify
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void shouldThrowErrorWhenValidateABalance() {
		Balance balance = new Balance();
		
		Throwable error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Description.");

		balance.setDescription("");
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Description.");
		
		balance.setDescription("Description");
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Month.");
		
		balance.setMonth(0);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Month.");
		
		balance.setMonth(13);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Month.");
		
		balance.setMonth(1);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Year.");
		
		balance.setYear(202);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Year..");
		
		balance.setMonth(2020);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a User.");
		
		balance.setUser(new User());

		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a User.");
		
		balance.getUser().setId(1L);
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a valid Value.");
		
		balance.setValue(BigDecimal.valueOf(10L));
		
		error = Assertions.catchThrowable(() -> service.validBalance(balance));
		Assertions.assertThat(error).isInstanceOf(RuleBusinessException.class).hasMessage("Enter a Balance Type.");
	}

}
