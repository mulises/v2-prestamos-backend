package com.mbaront.cobros.diarios.model.services;


import com.mbaront.cobros.diarios.model.entidades.Usuario;

public interface IUsuarioService{
	
	public Usuario findByUsername(String username);

}
