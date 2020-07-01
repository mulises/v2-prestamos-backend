package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IGastoDao;
import com.mbaront.cobros.diarios.model.entidades.Gasto;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

@Service
public class GastoServiceImpl implements IGastoService{
	
	@Autowired
	private IGastoDao gastoDao;

	@Override
	public Gasto save(Gasto gasto) {
		return gastoDao.save(gasto);
	}

	@Override
	public List<Gasto> findByRangoFechaAndRuta(Date fechaInicial, Date fechaFinal, Ruta ruta) {
		return gastoDao.findByFechaCreacionAfterAndFechaCreacionBeforeAndRuta(fechaInicial, fechaFinal, ruta);
	}

	@Override
	public Gasto findById(Long idGasto) {
		return gastoDao.findById(idGasto).orElse(null);
	}

	@Override
	public void eliminarGasto(Long idGasto) {
		gastoDao.deleteById(idGasto);
	}


}
