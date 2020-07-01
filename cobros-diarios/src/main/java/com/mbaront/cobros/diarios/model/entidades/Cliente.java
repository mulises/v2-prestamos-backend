package com.mbaront.cobros.diarios.model.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne()
	@JoinColumn(name = "entidad_id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Entidad entidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ruta_id", updatable = false, nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "clientes", "usuarios" })
	private Ruta ruta;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "cliente" })
	private List<Prestamo> prestamos;

	private Integer enrutamiento;

	private Date fechaCreacion;

	private boolean activo;
	
	

	public Cliente() {
	}

	@PrePersist
	private void prePersist() {
		setFechaCreacion(new Date());
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", entidad=" + entidad + ", ruta=" + ruta + ", prestamos=" + prestamos + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public void setRuta(Ruta ruta) {
		this.ruta = ruta;
	}

	public List<Prestamo> getPrestamos() {
		return prestamos;
	}

	public void setPrestamos(List<Prestamo> prestamos) {
		this.prestamos = prestamos;
	}

	public Integer getEnrutamiento() {
		return enrutamiento;
	}

	public void setEnrutamiento(Integer enrutamiento) {
		this.enrutamiento = enrutamiento;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
