package com.mbaront.cobros.diarios.model.dao;


import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;

public interface ICuadreDiarioDao extends CrudRepository<CuadreDiario, Long>{
	
	public CuadreDiario findByConfirmadoFalseAndCarteraId(Long idRuta);
	public List<CuadreDiario> findByFechaCreacionBetween(Date fechaInicial, Date fechaFinal);
		
}
