package com.mbaront.cobros.diarios.model.controladores;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificacionesController {
	
	@MessageMapping("/instruccion")
	@SendTo("/evento/instruccion")
	public String recibeInstruccion() {
		return "CONFIRMAR-CUADRE-DIARIO";
	}

}
