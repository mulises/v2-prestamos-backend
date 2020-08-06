package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import com.mbaront.cobros.diarios.model.entidades.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	
	public Cliente save(Cliente cliente);
	public Cliente findById(Long idCliente);
	public List<Cliente> saveAll(List<Cliente> clientes);
	public List<Cliente> findByMayorQue(Integer enrutamiento);
	public Cliente findByNumeroIdentificacionEntidad(String numeroIdentificacion);
	public List<Cliente> findByNombreOrApellidoOrIdentificacionActivoByCartera(String nombre,Long idCartera);
	public List<Cliente> findByNombreOrApellidoOrIdentificacion(String parametro);
	
}
