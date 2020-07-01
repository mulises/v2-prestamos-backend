package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IPrestamoService {
	
	public Prestamo save(Prestamo prestamo);
	public Prestamo findById(Long idPrestamo);
	public List<Prestamo> findByClienteAndActivoTrue(Cliente cliente);
	public List<Prestamo> reporteDiarioPrestamos(Long idRuta);
	public List<Prestamo> findAllActivoByRuta(Long idRuta);
	public List<Prestamo> findAllActivo();
	public List<Prestamo> findPrestamosByFechaInicioAndRuta(Date fechaInicio, Ruta ruta);
	
}
