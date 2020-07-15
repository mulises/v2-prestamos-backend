package com.mbaront.cobros.diarios.model.controladores;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.services.ICuadreDiarioService;
import com.mbaront.cobros.diarios.model.services.IPagoClienteService;

@RequestMapping("/api-prestamos")
@RestController
public class PagoClienteController {

	@Autowired
	private IPagoClienteService pagoClienteService;
	
	@Autowired 
	private ICuadreDiarioService cuadreDiarioService;

	@Secured("ROLE_SAVE_PAGO")
	@PostMapping("/pago-cliente")
	public ResponseEntity<?> save(@RequestBody PagoCliente pagoCliente) {
		Map<String,Object> response = new HashMap<>();
		
		CuadreDiario cuadreDiarioDB = validarCuadreDiarioActivo(pagoCliente.getPrestamo().getCliente().getRuta().getId());
		
		if(cuadreDiarioDB == null) {
			response.put("mensajeError", "No existe cuadre de caja activo!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		//Se verifica que no haya tenido pago el mismo día
		List<PagoCliente> pagosByPrestamo = pagoClienteService.pagosByPrestamo(pagoCliente.getPrestamo());
		
		if(!pagosByPrestamo.isEmpty()) {
			Comparator<PagoCliente> comparator = Comparator.comparing( PagoCliente::getFechaPago);
			PagoCliente ultimoPago  = pagosByPrestamo.stream().max(comparator).get();
			
			//int dias=(int) ((new Date().getTime()-ultimoPago.getFechaPago().getTime())/86400000);		
			
			
			//if(dias == 0) {
			if(compararFechaSinHora(new Date(), ultimoPago.getFechaPago())) {
				response.put("mensajeError", "El cliente reporta un pago realizado el día de hoy!");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
			}
		}
		
		
		PagoCliente pagoClienteNew = pagoClienteService.save(pagoCliente);		
		
		response.put("pagoCliente", pagoClienteNew);
		
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param idCartera
	 * @return Lista de pagos para un cuadre de caja
	 */
	@GetMapping("/lista-pagos-cuadre-activo/{idCuadreCaja}")
	public ResponseEntity<?> findByBetweenFecha(@PathVariable Long idCuadreCaja) {
		
		CuadreDiario cuadreCajaDB = cuadreDiarioService.findById(idCuadreCaja);  
		
		Date fechaFin = cuadreCajaDB.isConfirmado() ? cuadreCajaDB.getFechaConfirmacion() : new Date();
		
		List<PagoCliente> pagosCliente 
		= pagoClienteService.findByBetweenFecha(cuadreCajaDB.getFechaCreacion(), fechaFin, cuadreCajaDB.getCartera().getId());
		
		return new ResponseEntity<List<PagoCliente>>(pagosCliente,HttpStatus.OK);
	}
	
	private CuadreDiario validarCuadreDiarioActivo(Long idCartera) {
		CuadreDiario cuadreDiarioDB = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(idCartera);
		return cuadreDiarioDB;
	}
	
	private boolean compararFechaSinHora(Date date, Date hoy) {
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Calendar calHoy = Calendar.getInstance();
        calHoy.setTime(hoy);
        calHoy.set(Calendar.HOUR_OF_DAY, 0);
        calHoy.set(Calendar.MINUTE, 0);
        calHoy.set(Calendar.SECOND, 0);
        calHoy.set(Calendar.MILLISECOND, 0);
        
        int comparacion = cal.getTime().compareTo(calHoy.getTime());
		if(comparacion == 0)
			return false;
		else if(comparacion < 0)
			return true;
		else
			return false;
		
	}
}