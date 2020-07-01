package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Ruta;

public interface IRutaService {
	
	public List<Ruta> findAll();
	public Ruta findById(Long idRuta);
	public Ruta save(Ruta ruta);
	public List<Ruta> findByUsuario(String username);
	/**
	 * 
	 * @param idCartera
	 * @return Cartera sin lista de clientes ni usuarios asignados
	 */
	public Ruta findByIdSinUsuarioSinClientes(Long idCartera);

}
