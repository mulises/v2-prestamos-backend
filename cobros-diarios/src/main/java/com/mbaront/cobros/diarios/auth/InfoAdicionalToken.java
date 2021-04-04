package com.mbaront.cobros.diarios.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.mbaront.cobros.diarios.model.entidades.Usuario;
import com.mbaront.cobros.diarios.model.services.ParametroServiceImpl;
import com.mbaront.cobros.diarios.model.services.UsuarioService;


@Component
public class InfoAdicionalToken implements TokenEnhancer{

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ParametroServiceImpl parametroService;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		Map<String, Object> info = new HashMap<>();
		info.put("nombre", usuario.getEntidad().getNombres());
		info.put("codigo_empresa", parametroService.findByNombreParametro("CODIGO_EMPRESA").getValorString());
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}

}
