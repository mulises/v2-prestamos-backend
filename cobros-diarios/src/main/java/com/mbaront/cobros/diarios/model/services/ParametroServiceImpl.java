package com.mbaront.cobros.diarios.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IParametroDao;
import com.mbaront.cobros.diarios.model.entidades.Parametro;

@Service
public class ParametroServiceImpl implements IParametroService{
	
	@Autowired
	private IParametroDao parametroDao;

	@Override
	public Parametro findByNombreParametro(String nombreParametro) {
		return parametroDao.findByNombreParametro(nombreParametro);
	}

}
