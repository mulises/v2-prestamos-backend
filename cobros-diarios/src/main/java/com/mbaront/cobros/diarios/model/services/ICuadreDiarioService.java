package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface ICuadreDiarioService {
	
	public CuadreDiario findById(Long idCuadreDiario);
	public CuadreDiario saveCuadreDiario(CuadreDiario cuadreDiario);
	public CuadreDiario findCuadreDiarioActivoByIdRuta(Long idCartera);
	public List<CuadreDiario> findFechaCreacionBetween(Date fechaInicial, Date fechaFinal);
	public List<CuadreDiario> findByCarteraAndFechaCreacionBetween(Ruta cartera, Date fechaInicial, Date fechaFinal);
	public List<CuadreDiario> findAll();

}
