package com.bonejah.financialapi.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.enums.TypeBalance;
import com.bonejah.financialapi.models.Balance;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BalanceRepositoryTest {
	
	@Autowired
	BalanceRepository repository;
	
	@Autowired
	TestEntityManager entityManager;

	@Test
	public void shouldSaveABalance() {
		Balance balance = createBalance();
		
		balance = repository.save(balance);
		
		Assertions.assertThat(balance.getId()).isNotNull();
	}
	
	@Test
	public void shouldDeleteABalance() {
		Balance balance = createAndPersistABalance();
		
		balance = entityManager.find(Balance.class, balance.getId());
		
		repository.delete(balance);
		
		Balance balanceAbsent = entityManager.find(Balance.class, balance.getId());
		
		Assertions.assertThat(balanceAbsent).isNull();
	}
	
	@Test
	public void shouldUpdateABalance() {
		Balance balance = createAndPersistABalance();
		
		balance.setYear(2021);
		balance.setDescription("Teste Update Balance");
		balance.setStatus(StatusBalance.CANCELED);
		
		repository.save(balance);
		
		Balance balanceUpdated = entityManager.find(Balance.class, balance.getId());
		
		Assertions.assertThat(balanceUpdated.getYear()).isEqualTo(2021);
		Assertions.assertThat(balanceUpdated.getDescription()).isEqualTo("Test Update Balance");
		Assertions.assertThat(balanceUpdated.getStatus()).isEqualTo(StatusBalance.CANCELED);
	}

	@Test
	public void shouldFindABalanceById() {
		Balance balance = createAndPersistABalance();
		
		Optional<Balance> balanceFounded = repository.findById(balance.getId());
		
		Assertions.assertThat(balanceFounded.isPresent()).isTrue();
	}
	
	private Balance createAndPersistABalance() {
		Balance balance = createBalance();
		
		entityManager.persist(balance);
//		repository.save(lancamento);
		
		return balance;
	}

	public static Balance createBalance() {
		return Balance.builder()
			.year(2020)
			.month(8)
			.description("Balance Teste")
			.value(BigDecimal.valueOf(10))
			.type(TypeBalance.INCOME)
			.status(StatusBalance.PENDING)
			.dateRegister(LocalDateTime.now())
			.build();
	}
	
}
