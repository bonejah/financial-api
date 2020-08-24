package com.bonejah.financialapi.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.enums.TypeBalance;
import com.bonejah.financialapi.models.Balance;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

	@Query( value =
			"select sum (b.value) from Balance b join b.user u where u.id  = :idUser "
			+ "and b.type = :type "
			+ "and b.status = :status group by u")
	BigDecimal getBalanceByTypeBalanceAndUserIdAndStatusBalance(
			@Param("idUser") Long idUser, 
			@Param("type") TypeBalance type,
			@Param("status") StatusBalance status);
	
}
