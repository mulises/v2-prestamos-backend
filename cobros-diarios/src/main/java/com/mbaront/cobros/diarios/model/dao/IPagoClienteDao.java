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
	
}
