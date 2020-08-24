package com.bonejah.financialapi.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.models.Balance;

public interface BalanceService {
	
	public Balance save(Balance balance);

	public Balance update(Balance balance);
	
	void delete(Balance balance);
	
	List<Balance> getListBalance(Balance balance);
	
	void updateStatus(Balance balance, StatusBalance status);
	
	void validBalance(Balance balance);
	
	Optional<Balance> getBalanceById(Long id);
	
	BigDecimal getBalanceByIdUser(Long id);
	
}
