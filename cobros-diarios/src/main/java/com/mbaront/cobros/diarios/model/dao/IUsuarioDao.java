package com.mbaront.cobros.diarios.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.mbaront.cobros.diarios.model.entidades.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
	
	public Usuario findByUsername(String username);

}
