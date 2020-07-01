package com.mbaront.cobros.diarios.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Entidad;

public interface IEntidadDao extends CrudRepository<Entidad, Long>{
	
	public Entidad findByNumeroIdentificacion(String numeroIdentificacion);

}
