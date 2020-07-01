package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Entidad;

public interface IEntidadService {
	
	public List<Entidad> findAll();
	
	public Entidad findByNumeroIdentificacion(String numeroIdentificacion);
	
	public Entidad save(Entidad entidad);

}
