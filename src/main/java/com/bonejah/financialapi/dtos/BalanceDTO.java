package com.bonejah.financialapi.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {
	
	private Long id;
	private String description;
	private Integer month;
	private Integer year;
	private BigDecimal value;
	private Long userId;
	private String type;
	private String status;

}
