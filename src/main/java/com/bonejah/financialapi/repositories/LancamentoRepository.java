package com.bonejah.financialapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonejah.financialapi.models.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
