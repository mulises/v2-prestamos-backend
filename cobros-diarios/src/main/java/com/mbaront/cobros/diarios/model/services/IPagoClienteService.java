package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IPagoClienteService {

	public PagoCliente save(PagoCliente pagoCliente);

	public List<PagoCliente> reporteDiarioPago(Long idRuta);
	public List<PagoCliente> pagosByPrestamo(Prestamo prestamo);
	
	public List<PagoCliente> pagosByPeriodoTiempo(Date fechaInicio, Date fechaFin);
	public List<PagoCliente> findPagosByRutaAndFechaInicio(Date fechaInicio, Ruta ruta);
}
