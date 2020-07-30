package com.mbaront.cobros.diarios.model.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "prestamos")
public class Prestamo implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "prestamos", "usuarios", "fechaCreacion" })
	private Cliente cliente;

	@Column(name = "monto_prestamo")
	private Double montoPrestamo;

	@Column(name = "cantidad_cuota")
	private int cantidadCuota;

	@Column(name = "valor_cuota")
	private Double valorCuota;

	@Column(name = "fecha_prestamo")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPrestamo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prestamo")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "prestamo" })
	private List<PagoCliente> pagosCliente;

	private boolean activo;

	@Column(name = "porcentaje_prestamo")
	private Double porcentajePrestamo;

	private boolean ampliacion;

	@Column(name = "multa_ampliacion")
	private Double multaAmpliacion;

	@Column(name = "saldo_anterior")
	private Double saldoAnterior;

	@Column(name = "saldo_mora")
	private Double saldoMora;

	@Column(name = "periodicidad_cobro")
	private Integer periodicidadCobro;

	@Column(name = "valor_abono")
	private Double valorAbono;

	public Prestamo() {
		pagosCliente = new ArrayList<>();
	}

	@PrePersist
	private void prePersist() {
		setActivo(true);
		setFechaPrestamo(new Date());
		setSaldoMora(0D);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Double getMontoPrestamo() {
		return montoPrestamo;
	}

	public void setMontoPrestamo(Double montoPrestamo) {
		this.montoPrestamo = montoPrestamo;
	}

	public int getCantidadCuota() {
		return cantidadCuota;
	}

	public void setCantidadCuota(int cantidadCuota) {
		this.cantidadCuota = cantidadCuota;
	}

	public Date getFechaPrestamo() {
		return fechaPrestamo;
	}

	public void setFechaPrestamo(Date fechaPrestamo) {
		this.fechaPrestamo = fechaPrestamo;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Double getValorCuota() {
		return valorCuota;
	}

	public void setValorCuota(Double valorCuota) {
		this.valorCuota = valorCuota;
	}

	public List<PagoCliente> getPagosCliente() {
		return pagosCliente;
	}

	public void setPagosCliente(List<PagoCliente> pagosCliente) {
		this.pagosCliente = pagosCliente;
	}

	public Double getPorcentajePrestamo() {
		return porcentajePrestamo;
	}

	public void setPorcentajePrestamo(Double porcentajePrestamo) {
		this.porcentajePrestamo = porcentajePrestamo;
	}

	public Double getMultaAmpliacion() {
		return multaAmpliacion;
	}

	public void setMultaAmpliacion(Double multaAmpliacion) {
		this.multaAmpliacion = multaAmpliacion;
	}

	public boolean isAmpliacion() {
		return ampliacion;
	}

	public void setAmpliacion(boolean ampliacion) {
		this.ampliacion = ampliacion;
	}

	public Double getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(Double saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	public Double getSaldoMora() {
		return saldoMora;
	}

	public void setSaldoMora(Double saldoMora) {
		this.saldoMora = saldoMora;
	}

	public Integer getPeriodicidadCobro() {
		return periodicidadCobro;
	}

	public void setPeriodicidadCobro(Integer periodicidadCobro) {
		this.periodicidadCobro = periodicidadCobro;
	}

	public Double getValorAbono() {
		return valorAbono;
	}

	public void setValorAbono(Double valorAbono) {
		this.valorAbono = valorAbono;
	}

	public double getSaldoActual() {
		// el valor del porcentaje debe estar en la db como parametro
		double saldoActual = (getMontoPrestamo() * (1 + (getPorcentajePrestamo() / 100)) - getValorAbono());
		for (PagoCliente pagoCliente : pagosCliente) {
			saldoActual -= pagoCliente.getValorPago();
		}
		return saldoActual;
	}

	public PagoCliente getUltimoPago() {

		if (!pagosCliente.isEmpty()) {
			Comparator<PagoCliente> comparator = Comparator.comparing(PagoCliente::getFechaPago);
			PagoCliente ultimoPago = this.pagosCliente.stream().max(comparator).get();
			ultimoPago.setPrestamo(null);
			return ultimoPago;
		} else {
			return null;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
