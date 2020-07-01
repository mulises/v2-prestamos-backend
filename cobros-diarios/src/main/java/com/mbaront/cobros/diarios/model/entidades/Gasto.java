package com.mbaront.cobros.diarios.model.entidades;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "gastos")
public class Gasto implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fecha_creacion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreacion;

	@Column(name = "fecha_gasto")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaGasto;

	private String descripcion;

	@Column(name = "valor_gasto")
	private Double valorGasto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ruta_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "clientes", "usuarios" })
	private Ruta ruta;
	
	@PrePersist
	public void prePersist() {
		Date ahora = new Date();
        fechaCreacion = ahora;
        fechaGasto = ahora;
    }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaGasto() {
		return fechaGasto;
	}

	public void setFechaGasto(Date fechaGasto) {
		this.fechaGasto = fechaGasto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getValorGasto() {
		return valorGasto;
	}

	public void setValorGasto(Double valorGasto) {
		this.valorGasto = valorGasto;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public void setRuta(Ruta ruta) {
		this.ruta = ruta;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
