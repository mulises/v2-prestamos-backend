package com.mbaront.cobros.diarios.model.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.Empleado;
import com.mbaront.cobros.diarios.model.services.EmpleadoServiceImpl;

@RequestMapping("api-prestamos/")
@RestController
public class EmpleadoController {
	
	@Autowired
	private EmpleadoServiceImpl empleadoService;
	
	@GetMapping("/empleados")
	public List<Empleado> getEmpleados() {
		System.out.println("es por aqui ??");
		return empleadoService.findAll();		
	}

}
