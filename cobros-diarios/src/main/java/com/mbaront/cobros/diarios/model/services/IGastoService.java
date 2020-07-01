package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Gasto;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IGastoService {
	
	public Gasto save(Gasto gasto);
	public List<Gasto> findByRangoFechaAndRuta(Date fechaInicial, Date fechaFinal, Ruta ruta);
	public Gasto findById(Long idGasto);
	public void eliminarGasto(Long idGasto);

}
