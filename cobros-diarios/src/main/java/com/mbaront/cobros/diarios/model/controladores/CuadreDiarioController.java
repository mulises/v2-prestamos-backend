package com.mbaront.cobros.diarios.model.controladores;



import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.Gasto;
import com.mbaront.cobros.diarios.model.entidades.PagoCliente;
import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;
import com.mbaront.cobros.diarios.model.services.GastoServiceImpl;
import com.mbaront.cobros.diarios.model.services.ICuadreDiarioService;
import com.mbaront.cobros.diarios.model.services.IPagoClienteService;
import com.mbaront.cobros.diarios.model.services.IPrestamoService;
import com.mbaront.cobros.diarios.model.services.IRutaService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

@RestController
@RequestMapping("/api-prestamos/cuadaily")
public class CuadreDiarioController {

	
	@Autowired
	private ICuadreDiarioService cuadreDiarioService;
	@Autowired
	private IPagoClienteService pagoClienteService;
	@Autowired
	private IPrestamoService prestamoService;
	@Autowired
	private GastoServiceImpl gastoService;
	@Autowired
	private IRutaService rutaServiceImpl;
	
	
	@Secured("ROLE_SAVE_CUADRE_DIA")
	@PostMapping("/cuadre-diario")
	public ResponseEntity<?> saveCuadreDiario(@RequestBody CuadreDiario cuadreDiario) {
		Map<String, Object> response = new HashMap<>();

		CuadreDiario cuadreDiarioNew = cuadreDiarioService.saveCuadreDiario(cuadreDiario);

		response.put("cuadreDiario", cuadreDiarioNew);

		return new ResponseEntity<CuadreDiario>(cuadreDiarioNew, HttpStatus.OK);
	}

	@PutMapping("/cuadre-diario/{idCuadreDiario}")
	public ResponseEntity<?> updateCuadreDiario(@RequestBody CuadreDiario cuadreDiario,	@PathVariable Long idCuadreDiario) {
		Map<String, Object> response = new HashMap<>();

		CuadreDiario cuadreDiarioDB = cuadreDiarioService.findById(idCuadreDiario);

		if (cuadreDiarioDB.isConfirmado()) {
			response.put("mensajeError", "El cuadre no se puede actualizar, se encuentra confirmado!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		if (cuadreDiario.isConfirmado()) {
			cuadreDiarioDB.setConfirmado(cuadreDiario.isConfirmado());
			cuadreDiarioDB.setFechaConfirmacion(new Date());
			cuadreDiarioDB.setTotalPrestado(cuadreDiario.getTotalPrestado());
			cuadreDiarioDB.setTotalRecaudado(cuadreDiario.getTotalRecaudado());
			cuadreDiarioDB.setTotalMulta(cuadreDiario.getTotalMulta());
			cuadreDiarioDB.setTotalGasto(cuadreDiario.getTotalGasto());
		}

		cuadreDiarioDB.setValorBase(cuadreDiario.getValorBase());
		cuadreDiarioDB.setValorRealRecibido(cuadreDiario.getValorRealRecibido());
		cuadreDiarioDB.setObservaciones(cuadreDiario.getObservaciones());

		CuadreDiario cuadreDiarioNew = cuadreDiarioService.saveCuadreDiario(cuadreDiarioDB);

		response.put("cuadreDiario", cuadreDiarioNew);

		return new ResponseEntity<CuadreDiario>(cuadreDiarioNew, HttpStatus.OK);
	}
	
	@GetMapping("/cuadre-diario/{idCuadreDiario}")
	public ResponseEntity<?> getCuadreDiarioById(@PathVariable Long idCuadreDiario) {

		Map<String, Object> response = new HashMap<>();
		// El cuadre diario si no esta confirmado, no tiene los valore de resumen
		CuadreDiario cuadreDiario = cuadreDiarioService.findById(idCuadreDiario);
		
		
		if (cuadreDiario == null) {
			response.put("mensajeError", "Cuadre diario no existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		
		return new ResponseEntity<CuadreDiario>(cuadreDiario, HttpStatus.OK);

	}
	
	/**
	 * Busca el cuadre que se encuentre activo para la cartera enviada en parametro
	 * @param idCartera
	 * @return
	 */
	@GetMapping("/cuadre-activo-cartera/{idCartera}")
	public ResponseEntity<?> getCuadreDiarioActivoByIdRuta(@PathVariable Long idCartera) {

		Map<String, Object> response = new HashMap<>();
		// El cuadre diario si no esta confirmado, no tiene los valore de resumen
		CuadreDiario cuadreDiario = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(idCartera);
		
		
		if (cuadreDiario == null) {
			response.put("mensajeError", "La cartera no tiene cuadre activo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		
		return new ResponseEntity<CuadreDiario>(cuadreDiario, HttpStatus.OK);

	}
	
	// Metodo para validar opciones que dependan de un cuadre diario activo
	@GetMapping("/cuadre-activo-calc-resumen/{idRuta}")
	public ResponseEntity<?> getCuadreDiarioActivoCalcResumenByIdRuta(@PathVariable Long idRuta) {
		Map<String, Object> response = new HashMap<>();
		// El cuadre diario si no esta confirmado, no tiene los valore de resumen
		CuadreDiario cuadreDiario = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(idRuta);
		
		
		if (cuadreDiario == null) {
			response.put("mensajeError", "La ruta no tiene cuadre activo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		//se signan los valores de resumen al cuadre diario
		CuadreDiario cuadreDiarioReturn = setValoresResumen(cuadreDiario);
		
		return new ResponseEntity<CuadreDiario>(cuadreDiarioReturn, HttpStatus.OK);
	}
	
	@GetMapping("/cuadre-diario/historial/{fechaInicial}/{fechaFinal}")
	public ResponseEntity<?> getCuadreDiarioActivoByIdRuta(@PathVariable Date fechaInicial, @PathVariable Date fechaFinal) {

		List<CuadreDiario> cuadresDiario = cuadreDiarioService.findFechaCreacionBetween(fechaInicial, fechaFinal);
		
		return new ResponseEntity<List<CuadreDiario>>(cuadresDiario, HttpStatus.OK);

	}

	

	private CuadreDiario setValoresResumen(CuadreDiario cuadreDiario) {
		// Se calcula el valor total de las cuotas cobradas
		List<PagoCliente> pagosClientesDiario = pagoClienteService.findPagosByRutaAndFechaInicio(cuadreDiario.getFechaCreacion(),cuadreDiario.getCartera());
		double totalPagosCliente = 0;
		for (PagoCliente pagoCliente : pagosClientesDiario) {
			totalPagosCliente += pagoCliente.getValorPago();
		}

		// Se calcula el valor total de llos prestamos realizados
		List<Prestamo> prestamosClientesDiario = prestamoService.findPrestamosByFechaInicioAndRuta(cuadreDiario.getFechaCreacion(),cuadreDiario.getCartera());
		double totalPrestamos = 0;
		double totalMultasPrestamo = 0;
		double totalAbono = 0;
		for (Prestamo prestamo : prestamosClientesDiario) {
			totalPrestamos += prestamo.getMontoPrestamo();
			totalMultasPrestamo += prestamo.isAmpliacion() ? prestamo.getMultaAmpliacion() : 0;
			totalAbono += prestamo.getValorAbono();
		}
		Date fechaFin = cuadreDiario.getFechaConfirmacion() == null?new Date():cuadreDiario.getFechaConfirmacion();
		
		List<Gasto> gastosDiario = gastoService.findByRangoFechaAndRuta(cuadreDiario.getFechaCreacion(), fechaFin, cuadreDiario.getCartera());
		
		Double totalGastosCuadreDiario = gastosDiario.stream()
				.mapToDouble(Gasto::getValorGasto)
				.sum();
		
		cuadreDiario.setTotalMulta(totalMultasPrestamo);
		cuadreDiario.setTotalPrestado(totalPrestamos);
		cuadreDiario.setTotalRecaudado(totalPagosCliente);
		cuadreDiario.setTotalGasto(totalGastosCuadreDiario);
		cuadreDiario.setTotalAbono(totalAbono);
		
		return cuadreDiario;
	}
	
	@GetMapping("/download/{idCuadre}")
	public void getDocument(HttpServletResponse response, @PathVariable Long idCuadre) throws IOException, JRException, URISyntaxException  {
		
		CuadreDiario cuadreDiario = cuadreDiarioService.findById(idCuadre);
		
		List<PagoCliente> listaPagos = pagoClienteService.findPagosByRutaAndFechaInicio(cuadreDiario.getFechaCreacion(), cuadreDiario.getCartera());
		// creating datasource from bean list
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listaPagos);
				
		
		InputStream sourceFileName = this.getClass().getResourceAsStream("/reportes/jrxml/SampleJasperTemplate.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, null, beanColDataSource);		
		response.setContentType("application/x.xls");
		response.addHeader("Content-Disposition", "inline; filename=nombre_backend.xls;");
		
		JRXlsExporter exporterXLS = new JRXlsExporter();
		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);		
		
		exporterXLS.exportReport();		
		
	}
	
	@GetMapping("/flujo-caja/{idCartera}/{fechaInicio}/{fechaFin}")
	public void getDocumentFlujoCajaDiario(HttpServletResponse response, @PathVariable Long idCartera, @PathVariable Date fechaInicio,
			@PathVariable Date fechaFin) throws IOException, JRException, URISyntaxException  {
		
		
		Calendar fechaInicial = Calendar.getInstance();
		fechaInicial.setTime(fechaInicio);
		fechaInicial.set(Calendar.HOUR_OF_DAY, 0);
		fechaInicial.set(Calendar.MINUTE, 0);
		fechaInicial.set(Calendar.SECOND, 0);
        
        Calendar fechaFinal = Calendar.getInstance();
        fechaFinal.setTime(fechaFin);
        fechaFinal.set(Calendar.HOUR_OF_DAY, 23);
        fechaFinal.set(Calendar.MINUTE, 59);
        fechaFinal.set(Calendar.SECOND, 59);
                
        Ruta ruta = rutaServiceImpl.findById(idCartera);
        
		List<CuadreDiario> cuadresDiario = cuadreDiarioService.findByCarteraAndFechaCreacionBetween(ruta,fechaInicial.getTime(), fechaFinal.getTime());
		
			
		// creating datasource from bean list
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(cuadresDiario);
				
		
		InputStream sourceFileName = this.getClass().getResourceAsStream("/reportes/jrxml/flujo_caja_diario_cartera.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, null, beanColDataSource);		
		response.setContentType("application/x.xls");
		response.addHeader("Content-Disposition", "inline; filename=flujo_caja_diario_cartera.xls;");
		
		JRXlsExporter exporterXLS = new JRXlsExporter();
		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);		
		
		exporterXLS.exportReport();		
		
	}
	
}


