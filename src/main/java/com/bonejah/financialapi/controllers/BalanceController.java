package com.bonejah.financialapi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonejah.financialapi.dtos.BalanceDTO;
import com.bonejah.financialapi.dtos.StatusDTO;
import com.bonejah.financialapi.enums.StatusBalance;
import com.bonejah.financialapi.enums.TypeBalance;
import com.bonejah.financialapi.exceptions.RuleBusinessException;
import com.bonejah.financialapi.models.Balance;
import com.bonejah.financialapi.models.User;
import com.bonejah.financialapi.services.BalanceService;
import com.bonejah.financialapi.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/balances")
public class BalanceController {

	private final BalanceService balanceService;
	private final UserService userService;

//	public BalanceController(BalanceService balanceService, UserService userService) {
//		this.balanceService = balanceService;
//		this.userService = userService;
//	}
	
	@SuppressWarnings("unchecked")
	@PostMapping
	public ResponseEntity<?> save(@RequestBody BalanceDTO dto) {
		try {
			Balance balance = converterToObject(dto);
			balance = balanceService.save(balance);
			return new ResponseEntity(balance, HttpStatus.CREATED);
		} catch (RuleBusinessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody BalanceDTO dto) {
		return balanceService.getBalanceById(id).map(entity -> {
			try {
				Balance balance = converterToObject(dto);
				balance.setId(entity.getId());
				balanceService.update(balance);
				return ResponseEntity.ok(balance);
			} catch (RuleBusinessException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>("Balance not found in the database.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		return balanceService.getBalanceById(id).map(entity -> {
			balanceService.delete(entity);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity<Object>("Balance not found in the database.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/update-status")
	public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody StatusDTO dto) {
		return balanceService.getBalanceById(id).map(entity -> {
			StatusBalance statusSelected = StatusBalance.valueOf(dto.getStatus());

			if (statusSelected == null) {
				return ResponseEntity.badRequest().body("Could not update the status, please send a valid status.");
			}

			try {
				entity.setStatus(statusSelected);
				balanceService.update(entity);
				return ResponseEntity.ok(entity);
			} catch (RuleBusinessException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity<Object>("Balance not found in the database.", HttpStatus.BAD_REQUEST));
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("{id}")
	public ResponseEntity<?> getBalanceById(@PathVariable("id") Long id) {
		return balanceService.getBalanceById(id).map(balance -> new ResponseEntity(converterToDTO(balance), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public ResponseEntity<?> search(
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "year") Integer year,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "status", required = false) String status, 
			@RequestParam("userId") Long userId) {
		Balance balanceFilter = new Balance();
		balanceFilter.setDescription(description);
		balanceFilter.setMonth(month);
		balanceFilter.setYear(year);

		if (type != null) {
			balanceFilter.setType(TypeBalance.valueOf(type));
		}

		if (status != null) {
			balanceFilter.setStatus(StatusBalance.valueOf(status));
		}

		Optional<User> user = userService.getUserById(userId);

		if (!user.isPresent()) {
			return ResponseEntity.badRequest()
					.body("The query could not be performed. User not found for the given Id.");
		} else {
			balanceFilter.setUser(user.get());
		}

		List<Balance> balances = balanceService.getListBalance(balanceFilter);
		return ResponseEntity.ok(balances);
	}

	private BalanceDTO converterToDTO(Balance balance) {
		return BalanceDTO.builder().id(balance.getId()).description(balance.getDescription()).value(balance.getValue())
				.year(balance.getYear()).month(balance.getMonth()).status(balance.getStatus().name())
				.type(balance.getType().name()).userId(balance.getUser().getId()).build();
	}

	private Balance converterToObject(BalanceDTO dto) {
		Balance balance = new Balance();
		balance.setDescription(dto.getDescription());
		balance.setYear(dto.getYear());
		balance.setMonth(dto.getMonth());
		balance.setValue(dto.getValue());

		User userFounded = userService.getUserById(dto.getUserId())
				.orElseThrow(() -> new RuleBusinessException("Usuário não encontrado para o ID informado."));

		balance.setUser(userFounded);

		if (dto.getId() != null) {
			balance.setId(dto.getId());
		}

		if (dto.getType() != null) {
			balance.setType(TypeBalance.valueOf(dto.getType()));
		}

		if (dto.getStatus() != null) {
			balance.setStatus(StatusBalance.valueOf(dto.getStatus()));
		}

		return balance;
	}

}
