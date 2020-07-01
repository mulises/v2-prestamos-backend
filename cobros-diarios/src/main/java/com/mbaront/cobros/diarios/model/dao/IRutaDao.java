package com.mbaront.cobros.diarios.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IRutaDao extends CrudRepository<Ruta, Long>{
	
	//Este metodo se realiza para minimizar las consultas de las relaciones
	@Query("select NEW com.mbaront.cobros.diarios.model.entidades.Ruta(a.id, a.nombre,a.descripcion) from Ruta a join a.usuarios b where b.username = ?1")
	public List<Ruta> findByUsuario(String username);
	
	/**
	 * 
	 * @param idCartera
	 * @return Cartera sin lista de clientes ni usuarios asignados
	 */
	@Query("select NEW com.mbaront.cobros.diarios.model.entidades.Ruta(a.id, a.nombre,a.descripcion) from Ruta a where a.id = ?1")
	public Ruta findByIdSinUsuarioSinClientes(Long idCartera);

}
