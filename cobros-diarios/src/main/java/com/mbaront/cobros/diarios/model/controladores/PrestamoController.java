package com.mbaront.cobros.diarios.model.controladores;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.services.IClienteService;
import com.mbaront.cobros.diarios.model.services.ICuadreDiarioService;
import com.mbaront.cobros.diarios.model.services.IPagoClienteService;
import com.mbaront.cobros.diarios.model.services.IPrestamoService;

@RestController
@RequestMapping("/prestamo")
public class PrestamoController {

	@Autowired
	private IPrestamoService prestamoService;
	@Autowired
	private IClienteService clienteService;
	@Autowired
	private IPagoClienteService pagoClienteService;
	@Autowired
	private ICuadreDiarioService cuadreDiarioService;

	@Secured("ROLE_SAVE_PRESTAMO")
	@PostMapping("/prestamo")
	public ResponseEntity<?> save(@RequestBody Prestamo creditoRequest) {

		
		Map<String, Object> response = new HashMap<>();

		Cliente clientePrestamo = clienteService.findById(creditoRequest.getCliente().getId());
		
		if(!clientePrestamo.getPrestamos().stream().filter(prest -> prest.isActivo()).collect(Collectors.toList()).isEmpty()) {
			response.put("mensajeError", "El Cliente tiene prestamos activo, debe solicitar una ampliación");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		CuadreDiario cuadreDiario = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(clientePrestamo.getRuta().getId());
		
		//List<Prestamo> prestamosActivos = prestamoService.findByClienteAndActivoTrue(prestamoRequest.getCliente());
		
		double totalPagosClientesDia = totalPagosClientesDia(clientePrestamo);
		double totalPrestamosClientesDia = totalPrestamosClientesDia(clientePrestamo);
		
		double resumenCuadreDiario = (cuadreDiario.getValorBase() + totalPagosClientesDia - totalPrestamosClientesDia);
		double montoPrestamoRequest = creditoRequest.getMontoPrestamo();
		
		//si el porcentaje del prestamo es null por defecto se utiliza el parametrizado en la cartera
		creditoRequest.setPorcentajePrestamo(validarProcentajeCredito(creditoRequest));
				
		//el valor verdadero a entregar al prestador es el nuevo monto solicitado menos lo que estaba debiendo
		//y posiblemente el valor de la multa
		if(creditoRequest.isAmpliacion()) {
			montoPrestamoRequest -= (creditoRequest.getSaldoAnterior() - creditoRequest.getMultaAmpliacion());
		}
		
		if( resumenCuadreDiario < montoPrestamoRequest) {
			response.put("mensajeError", "No tiene fondos suficientes, contacte al supervisor");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}		

		Prestamo prestamoNew = prestamoService.save(creditoRequest);
		response.put("prestamo", prestamoNew);

		return new ResponseEntity<Prestamo>(prestamoNew, HttpStatus.OK);
	}
	
	@PostMapping("/ampliar-credito/{idCreditoAnterior}")
	public ResponseEntity<?> saveAmpliacion(@RequestBody Prestamo creditoRequest, @PathVariable Long idCreditoAnterior) {
		Map<String, Object> response = new HashMap<>();
		
		Prestamo creditoAFinalizar = prestamoService.findById(idCreditoAnterior);
		
		Cliente clientePrestamo = clienteService.findById(creditoRequest.getCliente().getId());
		CuadreDiario cuadreDiario = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(clientePrestamo.getRuta().getId());
		
		double totalPagosClientesDia = totalPagosClientesDia(clientePrestamo);
		double totalPrestamosClientesDia = totalPrestamosClientesDia(clientePrestamo);
		
		double resumenCuadreDiario = (cuadreDiario.getValorBase() + totalPagosClientesDia - totalPrestamosClientesDia);
		double montoPrestamoRequest = creditoRequest.getMontoPrestamo();
		montoPrestamoRequest -= (creditoRequest.getSaldoAnterior() + creditoRequest.getMultaAmpliacion());
		
		creditoRequest.setPorcentajePrestamo(validarProcentajeCredito(creditoRequest));
		creditoRequest.setAmpliacion(true);
		
		if( resumenCuadreDiario < montoPrestamoRequest) {
			response.put("mensajeError", "No tiene fondos suficientes, contacte al supervisor");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}	
		
		if(creditoRequest.isAmpliacion()) {
			PagoCliente pagoFinalizacion = new PagoCliente();
			pagoFinalizacion.setFechaPago(new Date());
			pagoFinalizacion.setPrestamo(creditoAFinalizar);
			pagoFinalizacion.setValorPago(creditoRequest.getSaldoAnterior());
			pagoClienteService.save(pagoFinalizacion);
		}
		
		Prestamo creditoNew = prestamoService.save(creditoRequest);
		response.put("prestamo", creditoNew);

		return new ResponseEntity<Prestamo>(creditoNew, HttpStatus.OK);
		
		
	}

	
	@PostMapping("/prestamo-activo-cliente")
	public ResponseEntity<?> findByClienteAndActivoTrue(@RequestBody Cliente cliente) {
		Map<String, Object> response = new HashMap<>();

		List<Prestamo> prestamosActivos = prestamoService.findByClienteAndActivoTrue(cliente);

		if (prestamosActivos.isEmpty()) {
			response.put("mensajeError", "El cliente no tiene prestamos activos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

		Prestamo prestamoActivo = prestamosActivos.get(0);
		return new ResponseEntity<Prestamo>(prestamoActivo, HttpStatus.OK);
	}
	
	@GetMapping("/prestamos-activo-ruta/{idRuta}")
	public ResponseEntity<?> findAllActivoByRuta(@PathVariable Long idRuta) {

		List<Prestamo> prestamosActivosByRuta = prestamoService.findAllActivoByRuta(idRuta);

		return new ResponseEntity<List<Prestamo>>(prestamosActivosByRuta, HttpStatus.OK);
	}
	
	@GetMapping("/prestamos-para-cobro/{idRuta}")
	public ResponseEntity<?> findAllPrestamosParaCobroByRuta(@PathVariable Long idRuta) {

		List<Prestamo> prestamosActivosByRuta = prestamoService.findAllActivoByRuta(idRuta);
		
		List<Prestamo> creditosParaCobroByRuta = prestamosActivosByRuta
				.stream()
				.filter(credito -> buscarDisponiblesParaCobro(credito))
				.collect(Collectors.toList());
				

		return new ResponseEntity<List<Prestamo>>(creditosParaCobroByRuta, HttpStatus.OK);
	}
	
	/**
	 * Lista los prestamos activos
	 * @return lista de prestamos activos
	 */
	@GetMapping("/presActs")
	public ResponseEntity<?> findAllPrestamosActivo(){
		
		Map<String,Object> response = new HashMap<String, Object>();
		
		List<Prestamo> listaPrestamosActivo = prestamoService.findAllActivo();
		
		response.put("listaPrestamos", listaPrestamosActivo);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/prest/{idPrestamo}")
	public ResponseEntity<?> getPrestamoById(@PathVariable Long idPrestamo) {
		Prestamo prestamoDB = prestamoService.findById(idPrestamo);
		
		if(prestamoDB == null) {
			return new ResponseEntity<>("Prestamo no existe ",HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Prestamo>(prestamoDB,HttpStatus.OK);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////METODOS PRIVADOS//////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	private Double totalPagosClientesDia(Cliente clientePrestamo) {
		
		// Se calcula el valor total de las cuotas cobradas
		List<PagoCliente> pagosClientesDiario = pagoClienteService.reporteDiarioPago(clientePrestamo.getRuta().getId());
		double totalPagosClientesDia = 0;
		for (PagoCliente pagoCliente : pagosClientesDiario) {
			totalPagosClientesDia += pagoCliente.getValorPago();
		}
		
		return totalPagosClientesDia;
	}
	
	private Double totalPrestamosClientesDia(Cliente clientePrestamo) {
		// Se calcula el valor total de llos prestamos realizados
		List<Prestamo> prestamosClientesDiario = prestamoService.reporteDiarioPrestamos(clientePrestamo.getRuta().getId());
		double totalPrestamosClientesDia = 0;
		for (Prestamo prestamo : prestamosClientesDiario) {
			totalPrestamosClientesDia += prestamo.getMontoPrestamo();
		}
		return totalPrestamosClientesDia;
	}
	
	private Double validarProcentajeCredito(Prestamo creditoRequest) {
		//si el porcentaje del prestamo es null por defecto se utiliza el parametrizado en la cartera
		if(creditoRequest.getPorcentajePrestamo() == null || creditoRequest.getPorcentajePrestamo() == 0) {
			return creditoRequest.getCliente().getRuta().getPorcentajePrestamo();
		}else {
			return creditoRequest.getPorcentajePrestamo();
		}
	}
	
	private boolean buscarDisponiblesParaCobro(Prestamo credito) {
		Date hoy = new Date();
		if(credito.getUltimoPago() == null) {
			if(compararFechaSinHora(credito.getFechaPrestamo(), hoy)<0) {
				return true;
			}
		}else {
			if(compararFechaSinHora(credito.getUltimoPago().getFechaPago(), hoy) < 0) {
				return true;
			}
		}
		return false;
	}
	
	private int compararFechaSinHora(Date fecha1, Date fecha2) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(fecha1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Calendar calHoy = Calendar.getInstance();
        calHoy.setTime(fecha2);
        calHoy.set(Calendar.HOUR_OF_DAY, 0);
        calHoy.set(Calendar.MINUTE, 0);
        calHoy.set(Calendar.SECOND, 0);
        calHoy.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime().compareTo(calHoy.getTime());
	}
}
