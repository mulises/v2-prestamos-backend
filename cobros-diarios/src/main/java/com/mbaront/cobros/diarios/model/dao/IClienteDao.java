package com.mbaront.cobros.diarios.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.Ruta;


public interface IClienteDao extends CrudRepository<Cliente, Long>{
	
	@Query("select a from Cliente a where a.entidad.numeroIdentificacion = ?1")
	public Cliente findByNumeroIdentificacionEntidad(String numeroIdentificacion);
	
	public List<Cliente> findByRutaAndEnrutamientoGreaterThanEqualOrderByEnrutamientoDesc(Ruta ruta, Integer enrutamiento);
	
	@Query("select a from Cliente a where (a.entidad.nombres like %?1% or a.entidad.apellidos like %?1% or a.entidad.numeroIdentificacion like %?1%) and a.activo = true and a.ruta.id = ?2")
	public List<Cliente> findByNombreOrApellidoOrIdentificacionActivoByCartera(String nombre,Long idCartera);
	
	/**
	 * busca por like %parametro% los campos, nombres, apellidos e identificacion 
	 * @param parametro
	 * @return lista de clientes
	 */
	@Query("select a from Cliente a where (a.entidad.nombres like %?1% or a.entidad.apellidos like %?1% or a.entidad.numeroIdentificacion like %?1%)")
	public List<Cliente> findByNombreOrApellidoOrIdentificacion(String parametro);
}
