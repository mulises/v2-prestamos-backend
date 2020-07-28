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
@Table(name = "pagos_clientes")
public class PagoCliente implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double valorPago;

	@Column(name = "fecha_pago")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPago;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prestamo_id")
	@JsonIgnoreProperties
	({ "hibernateLazyInitializer", "handler", "pagosCliente","ultimoPago"})
	private Prestamo prestamo;

	@PrePersist
	private void prePersist() {
		setFechaPago(new Date());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getValorPago() {
		return valorPago;
	}

	public void setValorPago(Double valorPago) {
		this.valorPago = valorPago;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
