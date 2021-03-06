package com.mbaront.cobros.diarios.model.services;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.dao.IPagoClienteDao;
import com.mbaront.cobros.diarios.model.dao.IPrestamoDao;
import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;

@Service
public class PagoClienteServiceImpl implements IPagoClienteService{

	@Autowired
	private IPagoClienteDao pagoClienteDao;
	
	@Autowired IPrestamoDao prestamoDao;
	
	@Override
	public PagoCliente save(PagoCliente pagoCliente) {
		
		PagoCliente pagoClienteNew = pagoClienteDao.save(pagoCliente);
		
		Prestamo prestamoDB = pagoClienteNew.getPrestamo();
		
		List<PagoCliente> pagosByPrestamo = pagosByPrestamo(prestamoDB);
		double totalPagosAlPrestamo = pagosByPrestamo
				.stream()
				.mapToDouble(PagoCliente::getValorPago)
				.sum();
		
		//double totalAPagarPorPrestamo = prestamoDB.getMontoPrestamo() * (1+prestamoDB.getPorcentajePrestamo()/100);
		
		//si el pago de la cuota es diferente al establecido se almacena como saldo en mora
		//positivo para saldo por cobrar
		//negativo para saldo a favor
		if(prestamoDB.getValorCuota() != pagoClienteNew.getValorPago()) {
			prestamoDB.setSaldoMora(prestamoDB.getSaldoMora()+(prestamoDB.getValorCuota() - pagoClienteNew.getValorPago()));
		}
		
		if((totalPagosAlPrestamo + prestamoDB.getValorAbono()) >= prestamoDB.getTotalPagar()) {
			prestamoDB.setActivo(false);
			prestamoDao.save(prestamoDB);
		}
		
		return pagoClienteNew;
	}

	@Override
	public List<PagoCliente> reporteDiarioPago(Long idRuta) {
		return pagoClienteDao.reporteDiarioPago(idRuta);
	}

	@Override
	public List<PagoCliente> pagosByPrestamo(Prestamo prestamo) {
		return pagoClienteDao.findByPrestamo(prestamo);
	}

	@Override
	public List<PagoCliente> pagosByPeriodoTiempo(Date fechaInicio, Date fechaFin) {
		return null;
	}

	@Override
	public List<PagoCliente> findPagosByRutaAndFechaInicio(Date fechaInicio, Ruta ruta) {
		return pagoClienteDao.findByFechaPagoAfterAndPrestamoClienteRuta(fechaInicio, ruta);
	}

	@Override
	public List<PagoCliente> findByBetweenFecha(Date fechaInicio, Date fechaFin, Long idCartera) {
		return pagoClienteDao.findByBetweenFecha(fechaInicio, fechaFin, idCartera);
	}

	@Override
	public void delete(Long idPagoCliente) {
		pagoClienteDao.deleteById(idPagoCliente);		
	}

	@Override
	public PagoCliente findById(Long idPagoCliente) {
		return pagoClienteDao.findById(idPagoCliente).orElse(null);
	}

}
