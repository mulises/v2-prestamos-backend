package com.mbaront.cobros.diarios.model.dao;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IPagoClienteDao extends CrudRepository<PagoCliente, Long>{
	
	@Query("select a from PagoCliente a where date(a.fechaPago) = curdate() and a.prestamo.cliente.ruta.id = ?1")
	public List<PagoCliente> reporteDiarioPago(Long idRuta);
	
	public List<PagoCliente> findByPrestamo(Prestamo prestamo);
	
	public List<PagoCliente> findByFechaPagoAfter(Date fechaInicio);
	public List<PagoCliente> findByFechaPagoAfterAndPrestamoClienteRuta(Date fechaInicio, Ruta ruta);
	
	@Query("select a from PagoCliente a where date(a.fechaPago) >= ?1 and date(a.fechaPago) <= ?2 and a.prestamo.cliente.ruta.id = ?3")
	public List<PagoCliente> findByBetweenFecha(Date fechaInicio,Date fechaFin, Long idRuta);
	
	
}
