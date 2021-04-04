package com.mbaront.cobros.diarios.model.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "parametros")
public class Parametro implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre_parametro")
	private String nombreParametro;

	@Column(name = "valor_str")
	private String valorString;

	@Column(name = "valor_num")
	private Double valorNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreParametro() {
		return nombreParametro;
	}

	public void setNombreParametro(String nombreParametro) {
		this.nombreParametro = nombreParametro;
	}

	public String getValorString() {
		return valorString;
	}

	public void setValorString(String valorString) {
		this.valorString = valorString;
	}

	public Double getValorNum() {
		return valorNum;
	}

	public void setValorNum(Double valorNum) {
		this.valorNum = valorNum;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
