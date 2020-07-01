package com.mbaront.cobros.diarios.model.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Gasto;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IGastoDao extends CrudRepository<Gasto, Long>{
	
	public List<Gasto> findByFechaCreacionAfterAndFechaCreacionBeforeAndRuta(Date fechaInicial, Date fechaFinal, Ruta ruta);

}
