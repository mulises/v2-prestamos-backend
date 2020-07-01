package com.mbaront.cobros.diarios.model.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.Entidad;
import com.mbaront.cobros.diarios.model.services.IEntidadService;

@RequestMapping("api-prestamos/")
@RestController
public class EntidadController {
	
	@Autowired
	private IEntidadService entidadService;
	
	
	@GetMapping("/entidad/{numeroIdentificacion}")
	public Entidad getEntidadNumeroIdentificacion(@PathVariable String numeroIdentificacion) {
		
		return entidadService.findByNumeroIdentificacion(numeroIdentificacion);
		
	}
	

}
