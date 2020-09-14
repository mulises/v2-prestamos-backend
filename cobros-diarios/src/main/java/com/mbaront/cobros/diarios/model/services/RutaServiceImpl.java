package com.mbaront.cobros.diarios.model.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbaront.cobros.diarios.model.dao.IRutaDao;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

@Service
public class RutaServiceImpl implements IRutaService{

	@Autowired
	private IRutaDao rutaDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Ruta> findAll() {
		return (List<Ruta>) rutaDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Ruta findById(Long idRuta) {
		return rutaDao.findById(idRuta).orElse(null);
	}
	
	/*
	 * Este metodo solo retorna el Id y Nombre de las rutas
	 * con el fin de obtimizar las consultas realizadas por las relaciones
	 */
	@Override
	public List<Ruta> findByUsuario(String username) {
		return rutaDao.findByUsuario(username);
	}


	@Override
	public Ruta save(Ruta ruta) {
		return rutaDao.save(ruta);
	}

	@Override
	public Ruta findByIdSinUsuarioSinClientes(Long idCartera) {
		return rutaDao.findByIdSinUsuarioSinClientes(idCartera);
	}

	@Override
	public List<Ruta> findByIdSinUsuarioSinClientes() {
		return rutaDao.findByIdSinUsuarioSinClientes();
	}	

}
