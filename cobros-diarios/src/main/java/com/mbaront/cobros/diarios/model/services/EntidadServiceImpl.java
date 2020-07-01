package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbaront.cobros.diarios.model.dao.IEntidadDao;
import com.mbaront.cobros.diarios.model.entidades.Entidad;

@Service
public class EntidadServiceImpl implements IEntidadService{

	@Autowired
	IEntidadDao entidadDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Entidad> findAll() {
		return (List<Entidad>) entidadDao.findAll();
	}

	@Override
	public Entidad findByNumeroIdentificacion(String numeroIdentificacion) {
		return entidadDao.findByNumeroIdentificacion(numeroIdentificacion);
	}

	@Override
	public Entidad save(Entidad entidad) {
		return entidadDao.save(entidad);
	}
	

}
