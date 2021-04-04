package com.mbaront.cobros.diarios.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Parametro;

public interface IParametroDao extends CrudRepository<Parametro, Long>{
	
	public Parametro findByNombreParametro(String nombreParametro);
}
