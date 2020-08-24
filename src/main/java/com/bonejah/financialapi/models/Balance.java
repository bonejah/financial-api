package com.bonejah.financialapi.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.enums.TypeBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance", schema = "financial_api")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "month")
	private Integer month;
	
	@Column(name = "year")
	private Integer year;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	@Column(name = "value")
	private BigDecimal value;
	
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "date_register")
	private LocalDateTime dateRegister;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "type")
	private TypeBalance type;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "status")
	private StatusBalance status;
	
}
