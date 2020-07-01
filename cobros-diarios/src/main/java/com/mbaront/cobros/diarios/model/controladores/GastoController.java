package com.mbaront.cobros.diarios.model.controladores;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.CuadreDiario;
import com.mbaront.cobros.diarios.model.entidades.Gasto;
import com.mbaront.cobros.diarios.model.entidades.Ruta;
import com.mbaront.cobros.diarios.model.services.GastoServiceImpl;
import com.mbaront.cobros.diarios.model.services.ICuadreDiarioService;
import com.mbaront.cobros.diarios.model.services.RutaServiceImpl;

@RestController
@RequestMapping("/api-prestamos/gasto")
public class GastoController {
	
	@Autowired
	private GastoServiceImpl gastoService;
	
	@Autowired
	private RutaServiceImpl rutaService;
	
	@Autowired 
	private ICuadreDiarioService cuadreDiarioService;
	
	@Secured("ROLE_SAVE_GASTO")
	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody Gasto gasto) {
		Map<String, Object> response = new HashMap<>();
		
		CuadreDiario cuadreDiarioDB = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(gasto.getRuta().getId());
		if(cuadreDiarioDB == null) {
			response.put("mensajeError", "No existe cuadre de caja activo!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		Gasto gastoNew = gastoService.save(gasto);
		
		
		
		response.put("gasto", gastoNew);
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@Secured("ROLE_SAVE_GASTO")
	@PutMapping("/update")
	public ResponseEntity<?> update(@RequestBody Gasto gasto) {
		Map<String, Object> response = new HashMap<>();
		
		CuadreDiario cuadreDiarioDB = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(gasto.getRuta().getId());
		if(cuadreDiarioDB == null) {
			response.put("mensajeError", "No existe cuadre de caja activo!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		
		Gasto gastoDB = gastoService.findById(gasto.getId());
		
		gastoDB.setDescripcion(gasto.getDescripcion());
		gastoDB.setValorGasto(gasto.getValorGasto());
		
		
		
		
		response.put("gasto", gastoService.save(gastoDB));
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	
	@GetMapping("listar-fechas-rutas/{fechaInicial}/{fechaFinal}/{idRuta}")
	public ResponseEntity<?> listarGastosRangoFechaByRuta(@PathVariable Long fechaInicial, @PathVariable Long fechaFinal, @PathVariable Long idRuta) {
		Map<String, Object> response = new HashMap<>();
		
		Ruta ruta = rutaService.findById(idRuta);
		
		if(ruta == null) {
			response.put("mensajeError", "La ruta no tiene cuadre activo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		Date fechaInicialDate = new Date(fechaInicial);
		Date fechaFinalDate = new Date(fechaFinal);
		
		List<Gasto> listaGastos = gastoService.findByRangoFechaAndRuta(fechaInicialDate, fechaFinalDate, ruta);
		
		return new ResponseEntity<List<Gasto>>(listaGastos, HttpStatus.OK);
		
	}
	
	/**
	 * Lista solo los gastos que correspondan al cuadre activo de la ruta, si la cartera no tiene cuadre activo devuelve lista vacia
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idRuta
	 * @return
	 */
	@GetMapping("listar-gastos-cartera/{idCartera}")
	public ResponseEntity<?> listarGastosByCuadreActivo(@PathVariable Long idCartera) {
		Map<String, Object> response = new HashMap<>();
		
		Ruta cartera = rutaService.findById(idCartera);
		
		if(cartera == null) {
			response.put("mensajeError", "No existe ruta seleccionada");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		CuadreDiario cuadreDiarioActivo = cuadreDiarioService.findCuadreDiarioActivoByIdRuta(idCartera);
		
		if(cuadreDiarioActivo == null) {
			response.put("mensajeError", "No existe cuadre activo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		List<Gasto> listaGastos = gastoService.findByRangoFechaAndRuta(cuadreDiarioActivo.getFechaCreacion(), new Date(), cartera);
		
		return new ResponseEntity<List<Gasto>>(listaGastos, HttpStatus.OK);
		
	}
	
	@GetMapping("/{idGasto}")
	public ResponseEntity<?> getGastoById(@PathVariable Long idGasto) {
		Map<String, Object> response = new HashMap<>();
		
		Gasto gasto = gastoService.findById(idGasto);
		
		if(gasto == null) {
			response.put("mensajeError", "Gasto con id " + idGasto +" no existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<Gasto>(gasto, HttpStatus.OK);
	}
	
	@DeleteMapping("/{idGasto}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminarGasto(@PathVariable Long idGasto) {
		gastoService.eliminarGasto(idGasto);		
	}
	
	

}
