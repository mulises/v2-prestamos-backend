package com.mbaront.cobros.diarios.model.controladores;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbaront.cobros.diarios.model.entidades.Cliente;
import com.mbaront.cobros.diarios.model.services.IClienteService;
import com.mbaront.cobros.diarios.model.services.IEntidadService;

@RequestMapping("api-prestamos/")
@RestController
public class ClienteController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IEntidadService entidadService;
	
	@GetMapping("/clientes")
	private List<Cliente> getClientes() {
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/{numeroIdentificacion}")
	public Cliente getCliente(@PathVariable String numeroIdentificacion) {
		return clienteService.findByNumeroIdentificacionEntidad(numeroIdentificacion);
	}
	
	/**
	 * Busca los clientes que contengan el parametro en nombres,apellidos o numero de identificacion
	 * @param numeroIdentificacion
	 * @param idCartera
	 * @return Lista de Cliente
	 */
	@GetMapping("/clientes-activo-like-by-cartera/{term}/{idCartera}")
	public List<Cliente> getClienteLikeCartera(@PathVariable String term, @PathVariable Long idCartera) {
		List<Cliente> clientesLike = clienteService.findByNombreOrApellidoOrIdentificacionActivoByCartera(term, idCartera);
		
		return clientesLike;
	}
	
	/**
	 * Busca los clientes que contengan el parametro en nombres,apellidos o numero de identificacion
	 * @param parametro
	 * @return Lista de Cliente
	 */
	@GetMapping("/clientes-like/{term}")
	public List<Cliente> getClientesLikeT(@PathVariable String term) {
		List<Cliente> clientesLike = clienteService.findByNombreOrApellidoOrIdentificacion(term);		
		return clientesLike;
	}
	
	@PostMapping("/cliente")
	private Cliente saveCliente(@RequestBody Cliente cliente) {
		
		if(cliente.getEntidad().getId() == null) {
			cliente.setEntidad(entidadService.save(cliente.getEntidad()));
		}
		
		if(cliente.getEnrutamiento() !=null) {
			List<Cliente> clientes = clienteService.findByMayorQue(cliente.getRuta(), cliente.getEnrutamiento());
			clientes.forEach(client -> client.setEnrutamiento(client.getEnrutamiento()+1));
			clienteService.saveAll(clientes);
			return clienteService.save(cliente);
		}else {
			return clienteService.save(cliente);
		}
		
	}
	
	@PutMapping("/update-cliente/{idCliente}")
	private ResponseEntity<?> updateCliente(@RequestBody Cliente cliente, @PathVariable Long idCliente) {
		Cliente clienteDB = clienteService.findById(idCliente);
		
		Cliente clienteByIdentificacion = clienteService.findByNumeroIdentificacionEntidad(cliente.getEntidad().getNumeroIdentificacion()); 
		
		if( clienteByIdentificacion != null && clienteByIdentificacion.getId() != clienteDB.getId()) {
			return new ResponseEntity<>("El numero de identificación existe para:  " + clienteByIdentificacion.getEntidad().getNombres() + " " + clienteByIdentificacion.getEntidad().getApellidos() ,HttpStatus.NOT_FOUND);
		}
		if(clienteDB.getEnrutamiento() != cliente.getEnrutamiento()) {
			List<Cliente> clientes = clienteService.findByMayorQue(cliente.getRuta(),cliente.getEnrutamiento());
			clientes.forEach(client -> {
				client.setEnrutamiento(client.getEnrutamiento()+1);
				clienteService.save(client);
			});
		}
		
		clienteDB.setActivo(cliente.isActivo());
		clienteDB.getEntidad().setNombres(cliente.getEntidad().getNombres());
		clienteDB.getEntidad().setApellidos(cliente.getEntidad().getApellidos());
		clienteDB.getEntidad().setDireccion(cliente.getEntidad().getDireccion());
		clienteDB.getEntidad().setTelefono(cliente.getEntidad().getTelefono());
		clienteDB.getEntidad().setNumeroIdentificacion(cliente.getEntidad().getNumeroIdentificacion());
		clienteDB.setEnrutamiento(cliente.getEnrutamiento());
		return new ResponseEntity<Cliente>(clienteService.save(clienteDB), HttpStatus.OK);
	}

}
	