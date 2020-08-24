package com.bonejah.financialapi.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.enums.TypeBalance;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.Balance;
import com.bonejah.financialapi.repositories.BalanceRepository;
import com.bonejah.financialapi.services.BalanceService;

@Service
public class BalanceServiceImpl implements BalanceService {
	
	private BalanceRepository repository;
	
	public BalanceServiceImpl(BalanceRepository repository) {
		this.repository = repository;
	}

	@Transactional
	@Override
	public Balance save(Balance balance) {
		validBalance(balance);
		balance.setStatus(StatusBalance.PENDING);
		return repository.save(balance);
	}

	@Transactional
	@Override
	public Balance update(Balance balance) {
		Objects.requireNonNull(balance.getId());
		validBalance(balance);
		return repository.save(balance);
	}

	@Transactional
	@Override
	public void delete(Balance balance) {
		Objects.requireNonNull(balance.getId());
		repository.delete(balance);
	}

	@Override
	public List<Balance> getListBalance(Balance balance) {
		Example<Balance> example = Example.of(balance, 
							ExampleMatcher.matching()
								.withIgnoreCase()
								.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	public void updateStatus(Balance balance, StatusBalance status) {
		balance.setStatus(status);
		update(balance);
	}
	
	@Override
	public void validBalance(Balance balance) {
		if (balance.getDescription() == null || balance.getDescription().trim().equals("")) {
			throw new RuleBusinessException("Enter a valid Description.");
		}
		
		if (balance.getMonth() == null || balance.getMonth() < 1 || balance.getMonth() > 12) {
			throw new RuleBusinessException("Enter a valid Month.");
		}

		if (balance.getYear() == null || balance.getYear().toString().length() != 4) {
			throw new RuleBusinessException("Enter a valid Year.");
		}
		
		if (balance.getUser() == null || balance.getUser().getId() == null) {
			throw new RuleBusinessException("Enter a User.");
		}
		
		if (balance.getValue() == null || balance.getValue().compareTo(BigDecimal.ZERO) < 1) {
			throw new RuleBusinessException("Enter a valid Value.");
		}
		
		if (balance.getType() == null) {
			throw new RuleBusinessException("Enter a Balance Type.");
		}
	}

	@Override
	public Optional<Balance> getBalanceById(Long id) {
		return repository.findById(id);
	}

	@Transactional()
	@Override
	public BigDecimal getBalanceByIdUser(Long id) {
		BigDecimal incomes = repository.getBalanceByTypeBalanceAndUserIdAndStatusBalance(id, TypeBalance.INCOME, StatusBalance.EFFECTIVE);
		BigDecimal expenses = repository.getBalanceByTypeBalanceAndUserIdAndStatusBalance(id, TypeBalance.EXPENSE, StatusBalance.EFFECTIVE);
		
		if (incomes ==null) {
			incomes = BigDecimal.ZERO;
		}
		
		if (expenses ==null) {
			expenses = BigDecimal.ZERO;
		}
		
		return  incomes.subtract(expenses);
	}

}
