package com.mbaront.cobros.diarios.model.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IPrestamoDao;
import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

@Service
public class PrestamoServiceImpl implements IPrestamoService{
	
	@Autowired
	private IPrestamoDao prestamoDao;
	
	@Override
	public Prestamo save(Prestamo prestamo) {
		return prestamoDao.save(prestamo);
	}

	@Override
	public Prestamo findById(Long idPrestamo) {
		return prestamoDao.findById(idPrestamo).orElse(null);
	}

	@Override
	public List<Prestamo> findByClienteAndActivoTrue(Cliente cliente) {
		return prestamoDao.findByClienteAndActivoTrue(cliente);
	}

	@Override
	public List<Prestamo> reporteDiarioPrestamos(Long idRuta) {
		return prestamoDao.reporteDiarioPrestamos(idRuta);
	}

	@Override
	public List<Prestamo> findAllActivoByRuta(Long idRuta) {
		return prestamoDao.findAllActivoByRuta(idRuta);
	}

	@Override
	public List<Prestamo> findAllActivo() {
		return prestamoDao.findAllByActivoTrue();
	}

	@Override
	public List<Prestamo> findPrestamosByFechaInicioAndRuta(Date fechaInicio, Ruta ruta) {
		return prestamoDao.findByFechaPrestamoAfterAndClienteRuta(fechaInicio, ruta);
	}

}
