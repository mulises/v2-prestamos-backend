package com.mbaront.cobros.diarios.model.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IPrestamoDao extends CrudRepository<Prestamo, Long>{
	
	public List<Prestamo> findByClienteAndActivoTrue(Cliente cliente);
	
	public List<Prestamo> findAllByActivoTrue();
	
	@Query("select a from Prestamo a where date(a.fechaPrestamo) = curdate() and a.cliente.ruta.id = ?1")
	public List<Prestamo> reporteDiarioPrestamos(Long idRuta);
	
	@Query("select a from Prestamo a join a.cliente b where a.activo = true and b.ruta.id = ?1 order by b.enrutamiento asc")
	public List<Prestamo> findAllActivoByRuta(Long idRuta);
	
	public List<Prestamo> findByFechaPrestamoAfterAndClienteRuta(Date fechaInicio, Ruta ruta);
	
	@Query("select a from Prestamo a where date(a.fechaPrestamo) >= ?1 and date(a.fechaPrestamo) <= ?2 and a.cliente.ruta.id = ?3")
	public List<Prestamo> findPrestamoBetweenFecha(Date fechaInicio,Date fechaFin,Long idCartera);
	
}
