package com.bonejah.financialapi.services;

import java.util.List;
import java.util.Optional;

import com.bonejah.financialapi.enums.StatusLancamento;
import com.bonejah.financialapi.models.Lancamento;

public interface LancamentoService {
	
	public Lancamento salvar(Lancamento lancamento);
	public Lancamento atualizar(Lancamento lancamento);
	void deletar(Lancamento lancamento);
	List<Lancamento> buscar(Lancamento lancamento);
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	void validar(Lancamento lancamento);
	Optional<Lancamento> obterPorId(Long id);
	
}
