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
@Table(name = "cuadres_diario")
public class CuadreDiario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ruta_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "clientes", "empleados", "usuarios" })
	private Ruta cartera;

	@Column(name = "fecha_creacion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreacion;

	@Column(name = "valor_base")
	private Double valorBase;

	@Column(name = "total_recaudado")
	private Double totalRecaudado;

	@Column(name = "total_prestado")
	private Double totalPrestado;

	@Column(name = "total_multa")
	private Double totalMulta;

	@Column(name = "total_gasto")
	private Double totalGasto;

	@Column(name = "total_abono")
	private Double totalAbono;

	@Column(name = "valor_real_recibido")
	private Double valorRealRecibido;

	private boolean confirmado;

	@Column(name = "fecha_confirmacion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaConfirmacion;

	private String observaciones;

	public CuadreDiario() {
		// TODO Auto-generated constructor stub
	}

	@PrePersist
	private void prePersist() {
		setFechaCreacion(new Date());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ruta getCartera() {
		return cartera;
	}

	public void setCartera(Ruta cartera) {
		this.cartera = cartera;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Double getValorBase() {
		return valorBase;
	}

	public void setValorBase(Double valorBase) {
		this.valorBase = valorBase;
	}

	public Double getTotalRecaudado() {
		return totalRecaudado;
	}

	public void setTotalRecaudado(Double totalRecaudado) {
		this.totalRecaudado = totalRecaudado;
	}

	public Double getTotalPrestado() {
		return totalPrestado;
	}

	public void setTotalPrestado(Double totalPrestado) {
		this.totalPrestado = totalPrestado;
	}

	public Double getTotalMulta() {
		return totalMulta;
	}

	public void setTotalMulta(Double totalMulta) {
		this.totalMulta = totalMulta;
	}

	public Double getTotalGasto() {
		return totalGasto;
	}

	public void setTotalGasto(Double totalGasto) {
		this.totalGasto = totalGasto;
	}

	public Double getTotalAbono() {
		return totalAbono;
	}

	public void setTotalAbono(Double totalAbono) {
		this.totalAbono = totalAbono;
	}

	public Double getValorRealRecibido() {
		return valorRealRecibido;
	}

	public void setValorRealRecibido(Double valorRealRecibido) {
		this.valorRealRecibido = valorRealRecibido;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

	public Date getFechaConfirmacion() {
		return fechaConfirmacion;
	}

	public void setFechaConfirmacion(Date fechaConfirmacion) {
		this.fechaConfirmacion = fechaConfirmacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
