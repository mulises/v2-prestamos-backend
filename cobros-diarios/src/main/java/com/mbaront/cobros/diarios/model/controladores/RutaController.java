package com.mbaront.cobros.diarios.model.controladores;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.Prestamo;
import com.mbaront.cobros.diarios.model.entidades.Ruta;
import com.mbaront.cobros.diarios.model.services.IPrestamoService;
import com.mbaront.cobros.diarios.model.services.IRutaService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

@RequestMapping("api-prestamos/")
@RestController
public class RutaController {
	
	@Autowired
	private IRutaService rutaService;
	
	@Autowired
	private IPrestamoService prestamoService;
	
	/**
	 * Lista de rutas asignadas a un usuario
	 * @param username
	 * @return Lista de rutas asignadas a un usuario
	 */
	@Secured("ROLE_LISTAR_CARTERAS")
	@GetMapping("/rutas-id-nombre/{username}")
	public ResponseEntity<?> getRutasIdNombre(@PathVariable String username) {
		
		Map<String,Object> response = new HashMap<>();
		response.put("rutas",rutaService.findByUsuario(username));
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/rutas")
	public List<Ruta> getRutas() {
		return rutaService.findAll();
	}
	
	@GetMapping("/informe-global-carteras")
	public ResponseEntity<?> informeGlobalCarteras() {
		List<Ruta> carterasDB = rutaService.findByIdSinUsuarioSinClientes();
		
		Map<String,Object> carteraMap;
		List<Map<String,Object>> listaCarteraMap = new ArrayList<Map<String,Object>>();
		
		for(Ruta cartera : carterasDB) {
			carteraMap = new HashMap<>();
			carteraMap.put("id", cartera.getId());
			carteraMap.put("nombreCartera", cartera.getNombre());
			
			List<Prestamo> listaPrestamosActivo = prestamoService.findAllActivoByRuta(cartera.getId());
			Double saldoPendienteTotal = listaPrestamosActivo.stream().mapToDouble(prestamo -> prestamo.getSaldoActual()).sum();
			carteraMap.put("saldoPendienteTotal", saldoPendienteTotal);
			carteraMap.put("cantidadPrestamosActivos", listaPrestamosActivo.size());		
			
			
			listaCarteraMap.add(carteraMap);
		}
		
		return new ResponseEntity<List<Map<String,Object>>>(listaCarteraMap,HttpStatus.OK);
		
	}
	
	/**
	 * Busca la ruta con el el id del parametro
	 * @param idRuta
	 * @return Cartera sin lista de clientes ni usuarios asignados
	 */
	@GetMapping("/cartera-sin-list-cliente/{idCartera}")
	public Ruta getRutaByIdSinClientes(@PathVariable Long idCartera) {
		Ruta carteraReturn = rutaService.findByIdSinUsuarioSinClientes(idCartera);
		return carteraReturn;
	}
	
	
	@Secured("ROLE_SAVE_CARTERA")
	@PostMapping("/ruta")
	public ResponseEntity<?> saveCartera(@RequestBody Ruta ruta) {
		Map<String,Object> response = new HashMap<>();
		response.put("cartera",rutaService.save(ruta));
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	
	@GetMapping("/cartera/prestamosActivos/{idCartera}")
	public void informePrestamosActivos(HttpServletResponse response, @PathVariable Long idCartera) throws IOException, JRException, URISyntaxException  {
		                
        Ruta rutaDB = rutaService.findByIdSinUsuarioSinClientes(idCartera);
        
		List<Prestamo> prestamosActivos = prestamoService.findAllActivoByRuta(idCartera);
		
			
		// creating datasource from bean list
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(prestamosActivos);
				
		
		InputStream sourceFileName = this.getClass().getResourceAsStream("/reportes/jrxml/informe_prestamos_activos_by_ruta.jasper");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("NOMBRERUTA", rutaDB.getNombre());
        parametros.put("FECHADESCARGA", new Date());
        
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, parametros, beanColDataSource);		
		response.setContentType("application/x.xls");
		response.addHeader("Content-Disposition", "inline; filename=prestamos_activo.xls;");
		
		JRXlsExporter exporterXLS = new JRXlsExporter();
		exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,new Boolean(true));
		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		
		exporterXLS.exportReport();		
		
	}

}
