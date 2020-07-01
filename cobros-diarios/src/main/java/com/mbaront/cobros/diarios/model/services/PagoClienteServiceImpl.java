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
		
		Prestamo prestamo = pagoClienteNew.getPrestamo();
		
		List<PagoCliente> pagosByPrestamo = pagosByPrestamo(prestamo);
		double totalPagosAlPrestamo = pagosByPrestamo
				.stream()
				.mapToDouble(PagoCliente::getValorPago)
				.sum();
		
		double totalAPagarPorPrestamo = 
				prestamo.getMontoPrestamo() * (1+prestamo.getPorcentajePrestamo()/100);
		
		//si el pago de la cuota es diferente al establecido se almacena como saldo en mora
		//positivo para saldo por cobrar
		//negativo para saldo a favor
		if(prestamo.getValorCuota() != pagoClienteNew.getValorPago()) {
			prestamo.setSaldoMora(prestamo.getSaldoMora()+(prestamo.getValorCuota() - pagoClienteNew.getValorPago()));
		}
		
		if(totalPagosAlPrestamo >= totalAPagarPorPrestamo) {
			prestamo.setActivo(false);
			prestamoDao.save(prestamo);
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

}
