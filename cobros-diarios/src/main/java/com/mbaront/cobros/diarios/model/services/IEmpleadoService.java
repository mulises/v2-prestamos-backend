package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Empleado;

public interface IEmpleadoService {

	public List<Empleado> findAll();
}
