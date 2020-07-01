package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IEmpleadoDao;
import com.mbaront.cobros.diarios.model.entidades.Empleado;

@Service
public class EmpleadoServiceImpl implements IEmpleadoService{

	@Autowired
	private IEmpleadoDao empleadoDao;
	
	@Override
	public List<Empleado> findAll() {
		return (List<Empleado>) empleadoDao.findAll();
	}

}
