package com.mbaront.cobros.diarios.model.controladores;

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

import com.mbaront.cobros.diarios.model.entidades.Ruta;
import com.mbaront.cobros.diarios.model.services.IRutaService;

@RequestMapping("api-prestamos/")
@RestController
public class RutaController {
	
	@Autowired
	private IRutaService rutaService;
	
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

}
