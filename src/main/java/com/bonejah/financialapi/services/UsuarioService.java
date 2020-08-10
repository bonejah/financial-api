package com.bonejah.financialapi.services;

import com.bonejah.financialapi.models.Usuario;

public interface UsuarioService {	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
}
