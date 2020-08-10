package com.bonejah.financialapi.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonejah.financialapi.exceptions.ErroAutenticacaoException;
import com.bonejah.financialapi.exceptions.RegraNegocioException;
import com.bonejah.financialapi.models.Usuario;
import com.bonejah.financialapi.repositories.UsuarioRepository;
import com.bonejah.financialapi.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não encontrado.");
		}
		
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Usuário/Inválido não encontrado.");
		}
			
		return usuario.get();
	}

	@Transactional
	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean exists = repository.existsByEmail(email);
		
		if(exists) {
			throw new RegraNegocioException("Já existe um usuário cadsatrado com este email.");
		}
	}

}
