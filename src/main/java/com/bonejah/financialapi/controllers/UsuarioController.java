package com.bonejah.financialapi.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonejah.financialapi.dtos.UsuarioDTO;
import com.bonejah.financialapi.exceptions.ErroAutenticacaoException;
import com.bonejah.financialapi.exceptions.RegraNegocioException;
import com.bonejah.financialapi.models.Usuario;
import com.bonejah.financialapi.services.LancamentoService;
import com.bonejah.financialapi.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private UsuarioService usuarioService;
	private LancamentoService lancamentoService;
	
	public UsuarioController(UsuarioService service, LancamentoService lancamentoService) {
		this.usuarioService = service;
		this.lancamentoService = lancamentoService;
	}
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder()
							.email(dto.getEmail())
							.nome(dto.getNome())
							.senha(dto.getSenha()).build();
		
		try {
			Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
		try {
			Usuario usuarioAutenticado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity<?> obterSaldo(@PathVariable("id") Long id) {
		Optional<Usuario> usuarioEncontrado = usuarioService.obterPorId(id);
		
		if (!usuarioEncontrado.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
	
	@GetMapping("/")
	public String helloWorld() {
		return "Hello World";
	}
}
