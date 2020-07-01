package com.mbaront.cobros.diarios.model.entidades;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "rutas")
public class Ruta implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	private String descripcion;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "usuario_rutas", inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler","rutas" })
	private List<Usuario> usuarios;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ruta")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler","ruta" })
	private List<Cliente> clientes;

	@Column(name = "porcentaje_prestamo")
	private Double porcentajePrestamo;

	public Ruta() {
	}

	public Ruta(Long id, String nombre, String descripcion) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Double getPorcentajePrestamo() {
		return porcentajePrestamo;
	}

	public void setPorcentajePrestamo(Double porcentajePrestamo) {
		this.porcentajePrestamo = porcentajePrestamo;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
