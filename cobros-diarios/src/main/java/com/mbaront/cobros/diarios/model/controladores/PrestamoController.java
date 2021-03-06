package com.mbaront.cobros.diarios.model.controladores;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.services.CalculoTotalCuotaPrestamo;
import com.mbaront.cobros.diarios.model.services.IClienteService;
import com.mbaront.cobros.diarios.model.services.ICuadreDiarioService;
import com.mbaront.cobros.diarios.model.services.IPagoClienteService;
import com.mbaront.cobros.diarios.model.services.IPrestamoService;
import com.mbaront.cobros.diarios.model.services.ParametroServiceImpl;

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
	@Autowired
	private CalculoTotalCuotaPrestamo calculoEmpresas;
	@Autowired
	private ParametroServiceImpl parametroService;

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
		
		double totalPagosClientesDia = totalPagosClientesDia(cuadreDiario);
		double totalPrestamosClientesDia = totalPrestamosClientesDia(cuadreDiario);
		
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
		
		Prestamo prestamoNew = null;
		
		try	{
			Map<String,Double> valoresCalculadosEmpresa = (calculoEmpresas.calcular(creditoRequest, parametroService.findByNombreParametro("CODIGO_EMPRESA").getValorString()));
			creditoRequest.setValorCuota(valoresCalculadosEmpresa.get("valorCuota"));
			creditoRequest.setTotalPagar(valoresCalculadosEmpresa.get("totalPagar"));
			prestamoNew = prestamoService.save(creditoRequest);
			response.put("prestamo", prestamoNew);
		}catch (Exception e) {
			e.printStackTrace();
			response.put("mensajeError", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Prestamo>(prestamoNew, HttpStatus.OK);
	}
	
	@PostMapping("/ampliar-credito/{idCreditoAnterior}")
	public ResponseEntity<?> saveAmpliacion(@RequestBody Prestamo creditoRequest, @PathVariable Long idCreditoAnterior) {
		Map<String, Object> response = new HashMap<>();
		
		Prestamo creditoAFinalizar = prestamoService.findById(idCreditoAnterior);
		
		Cliente clientePrestamo = clienteService.findById(creditoRequest.getCliente().getId());
		CuadreDiario cuadreDiario = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(clientePrestamo.getRuta().getId());
		
		double totalPagosClientesDia = totalPagosClientesDia(cuadreDiario);
		double totalPrestamosClientesDia = totalPrestamosClientesDia(cuadreDiario);
		
		double resumenCuadreDiario = (cuadreDiario.getValorBase() + totalPagosClientesDia - totalPrestamosClientesDia);
		double montoPrestamoRequest = creditoRequest.getMontoPrestamo();
		montoPrestamoRequest -= (creditoRequest.getSaldoAnterior() + creditoRequest.getMultaAmpliacion());
		
		creditoRequest.setPorcentajePrestamo(validarProcentajeCredito(creditoRequest));
		creditoRequest.setAmpliacion(true);
		
		if( resumenCuadreDiario < montoPrestamoRequest) {
			response.put("mensajeError", "No tiene fondos suficientes, contacte al supervisor");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}	
		
		Prestamo creditoNew = prestamoService.save(creditoRequest);
		response.put("prestamo", creditoNew);
		
		if(creditoRequest.isAmpliacion()) {
			PagoCliente pagoFinalizacion = new PagoCliente();
			pagoFinalizacion.setFechaPago(new Date());
			pagoFinalizacion.setPrestamo(creditoAFinalizar);
			pagoFinalizacion.setValorPago(creditoRequest.getSaldoAnterior());
			pagoClienteService.save(pagoFinalizacion);
		}
		

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
	
	/**
	 * 
	 * @param idRuta
	 * @return Prestamos Activos por la ruta
	 */
	@GetMapping("/prestamos-activo-ruta/{idRuta}")
	public ResponseEntity<?> findAllActivoByRuta(@PathVariable Long idRuta) {

		List<Prestamo> prestamosActivosByRuta = prestamoService.findAllActivoByRuta(idRuta);

		return new ResponseEntity<List<Prestamo>>(prestamosActivosByRuta, HttpStatus.OK);
	}
	
	/**
	 * Lista de creditos sin cobros en un cuadre de caja
	 * @param idRuta
	 * @return lista de creditos que no han realizado pagos dentro del cuadre de caja activo
	 */
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
	
	/**
	 * 
	 * @param idCuadreDiario
	 * @return lista de prestamos realizados entre el rango de fechas de un cuadre diario (fecha creacion - fecha confirmacion)
	 */
	@GetMapping("lista-prestamos/cuadre/{idCuadreDiario}")
	public ResponseEntity<?> getListaPrestamosPorCuadre(@PathVariable Long idCuadreDiario) {
		
		CuadreDiario cuadreDiarioDB = cuadreDiarioService.findById(idCuadreDiario);
		
		Date fechaFin;
		
		if(cuadreDiarioDB.isConfirmado()) {
			fechaFin = cuadreDiarioDB.getFechaConfirmacion();
		}else {
			fechaFin = new Date();
		}
		
		List<Prestamo> prestamos = prestamoService.findPrestamoBetweenFecha(cuadreDiarioDB.getFechaCreacion(), fechaFin, cuadreDiarioDB.getCartera().getId());
		
		return new ResponseEntity<List<Prestamo>>(prestamos,HttpStatus.OK);		
		
	}
	
	
	@DeleteMapping("eliminar/{idPrestamo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminarPrestamo(@PathVariable Long idPrestamo) {
		prestamoService.eliminarPrestamo(idPrestamo);
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("imagen") MultipartFile imagen, @RequestParam("idPrestamo") Long idPrestamo) {
		Map<String,Object> response = new HashMap<String, Object>();
		
		if(!imagen.isEmpty()) {
			String nombreImagen = imagen.getOriginalFilename();
			FileSystem sistemaFicheros=FileSystems.getDefault();
			Path rutaArchivo = sistemaFicheros.getPath("/Users/liliamariajimenezrodriguez/Documents/mbaront/proyectos/prestapp/documentos_soportes/").resolve(nombreImagen).toAbsolutePath();
			
			try {
				Files.copy(imagen.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.put("mensaje", "Imagen subida correctamente");
		}
		
		
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);	
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////METODOS PRIVADOS//////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	private Double totalPagosClientesDia(CuadreDiario cuadreDiario) {
		
		
		Date fechaFin = new Date();
		
		if(cuadreDiario.getFechaConfirmacion() != null) {
			fechaFin = cuadreDiario.getFechaConfirmacion();
		}
		// Se calcula el valor total de las cuotas cobradas
		List<PagoCliente> pagosClientesDiario = 
				pagoClienteService.findByBetweenFecha(cuadreDiario.getFechaCreacion(),fechaFin,cuadreDiario.getCartera().getId());
		
		double totalPagosClientesDia = 0;
		for (PagoCliente pagoCliente : pagosClientesDiario) {
			totalPagosClientesDia += pagoCliente.getValorPago();
		}
		
		return totalPagosClientesDia;
	}
	
	private Double totalPrestamosClientesDia(CuadreDiario cuadreDiario) {
		
		Date fechaFin = new Date();
		
		if(cuadreDiario.getFechaConfirmacion() != null) {
			fechaFin = cuadreDiario.getFechaConfirmacion();
		}
		// Se calcula el valor total de los prestamos realizados
		List<Prestamo> prestamosClientesDiario = prestamoService.findPrestamoBetweenFecha(cuadreDiario.getFechaCreacion(),fechaFin, cuadreDiario.getCartera().getId());
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
		Calendar calendarUltimoPago = Calendar.getInstance();
		
		if(credito.getUltimoPago() == null) {
			if(compararFechaSinHora(credito.getFechaPrestamo(), hoy)<0) {
				return true;
			}
		}else {
			calendarUltimoPago.setTime(credito.getUltimoPago().getFechaPago());
			calendarUltimoPago.add(Calendar.DAY_OF_YEAR, credito.getPeriodicidadCobro());
			if(compararFechaSinHora(calendarUltimoPago.getTime(), hoy) <= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param fecha1
	 * @param fecha2
	 * @return 0 si son iguales; < 0 si fecha1 es anterior a fecha2; y > 0 si fecha1 es posterior a fecha2
	 */
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
