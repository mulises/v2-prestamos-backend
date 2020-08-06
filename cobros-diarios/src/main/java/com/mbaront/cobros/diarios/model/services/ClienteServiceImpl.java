package com.mbaront.cobros.diarios.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IClienteDao;
import com.mbaront.cobros.diarios.model.entidades.Cliente;

@Service
public class ClienteServiceImpl implements IClienteService{

	@Autowired
	private IClienteDao clienteDao;
	
	@Override
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	public Cliente save(Cliente cliente) {
		return clienteDao.save(cliente);
	}

	@Override
	public Cliente findByNumeroIdentificacionEntidad(String numeroIdentificacion) {
		return clienteDao.findByNumeroIdentificacionEntidad(numeroIdentificacion);
	}

	@Override
	public List<Cliente> saveAll(List<Cliente> clientes) {
		return  (List<Cliente>) clienteDao.saveAll(clientes);
	}

	@Override
	public List<Cliente> findByMayorQue(Integer enrutamiento) {
		return clienteDao.findByEnrutamientoGreaterThanEqualOrderByEnrutamientoDesc(enrutamiento);
	}

	@Override
	public Cliente findById(Long idCliente) {
		return clienteDao.findById(idCliente).orElse(null);
	}

	@Override
	public List<Cliente> findByNombreOrApellidoOrIdentificacionActivoByCartera(String nombre, Long idCartera) {
		return clienteDao.findByNombreOrApellidoOrIdentificacionActivoByCartera(nombre, idCartera);
	}

	@Override
	public List<Cliente> findByNombreOrApellidoOrIdentificacion(String parametro) {
		return clienteDao.findByNombreOrApellidoOrIdentificacion(parametro);
	}

}
