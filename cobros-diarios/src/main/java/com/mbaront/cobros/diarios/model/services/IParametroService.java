package com.mbaront.cobros.diarios.model.services;

import com.mbaront.cobros.diarios.model.entidades.Parametro;

public interface IParametroService {
	
	public Parametro findByNombreParametro(String nombreParametro);

}
