package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.ICuadreDiarioDao;
import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;

@Service
public class CuadreDiarioServiceImpl implements ICuadreDiarioService {
	
	@Autowired
	private ICuadreDiarioDao cuadreDiarioDao;

	@Override
	public CuadreDiario saveCuadreDiario(CuadreDiario cuadreDiario) {
		return cuadreDiarioDao.save(cuadreDiario);
	}

	@Override
	public CuadreDiario findCuadreDiarioActivoByIdRuta(Long idRuta) {
		return cuadreDiarioDao.findByConfirmadoFalseAndCarteraId(idRuta);
	}

	@Override
	public CuadreDiario findById(Long idCuadreDiario) {
		return cuadreDiarioDao.findById(idCuadreDiario).orElse(null);
	}

	@Override
	public List<CuadreDiario> findFechaCreacionBetween(Date fechaInicial, Date fechaFinal) {
		return cuadreDiarioDao.findByFechaCreacionBetween(fechaInicial, fechaFinal);
	}

	@Override
	public List<CuadreDiario> findAll() {
		return (List<CuadreDiario>) cuadreDiarioDao.findAll();
	}

}
