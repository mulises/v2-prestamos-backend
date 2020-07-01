package com.mbaront.cobros.diarios.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Empleado;

public interface IEmpleadoDao extends CrudRepository<Empleado, Long>{

}
