package com.mbaront.cobros.diarios.model.services;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mbaront.cobros.diarios.model.entidades.Prestamo;

@Service
public class CalculoTotalCuotaPrestamo {
	
	public Map<String, Double>calcular(Prestamo prestamo, String codigoEmpresa) {
		
		if("idancur".equalsIgnoreCase(codigoEmpresa))
			return calcularIdancur(prestamo);
		if("lanza".equalsIgnoreCase(codigoEmpresa))
			return calcularLanza(prestamo);
		else
			return null;
		
	}
	
	/**
	 * 
	 * @param prestamo
	 * @return un Map con el valor de la cuota y el total a pagar para el cliente inversiones dancur sas
	 */
	private Map<String, Double> calcularIdancur(Prestamo prestamo) {
		Map<String, Double> valoresCalculados = new HashMap<String, Double>();
		
		//calculo del total a pagar
		Double totalInteres = (prestamo.getMontoPrestamo() * (prestamo.getPorcentajePrestamo()/100))*prestamo.getCantidadCuota();	
		Double totalPagar = prestamo.getMontoPrestamo() + totalInteres;	
		valoresCalculados.put("totalPagar", totalPagar);
		
		//calculo de la cuota
		DecimalFormat df = new DecimalFormat("#.00");
		String cuotaRedondeada = df.format(totalPagar/prestamo.getCantidadCuota());
		valoresCalculados.put("valorCuota", Double.parseDouble(cuotaRedondeada));
		
		return valoresCalculados;
		
	}
	
	/**
	 * 
	 * @param prestamo
	 * @return un Map con el valor de la cuota y el total a pagar para el cliente lanza pagadiario
	 */
	private Map<String, Double> calcularLanza(Prestamo prestamo) {
		Map<String, Double> valoresCalculados = new HashMap<String, Double>();
		
		//calculo del total a pagar
		Double totalPagar = prestamo.getMontoPrestamo() * (1+(prestamo.getPorcentajePrestamo()/100));	
		valoresCalculados.put("totalPagar", totalPagar);
				
		//calculo de la cuota
		double miles = (totalPagar) / prestamo.getCantidadCuota();
	    double milesEntero = Math.floor(miles/1000);
	    double milesRedondeado = milesEntero * 1000;

	    if((miles-milesRedondeado)>0 ) {
	      milesRedondeado += 1000;
	    }
		
		valoresCalculados.put("valorCuota", milesRedondeado);
				
		return valoresCalculados;
		
	}

}
