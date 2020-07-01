package com.mbaront.cobros.diarios.model.services;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbaront.cobros.diarios.model.dao.IUsuarioDao;
import com.mbaront.cobros.diarios.model.entidades.Usuario;

@Service
public class UsuarioService implements UserDetailsService, IUsuarioService{

	@Autowired
	private IUsuarioDao usuarioDao;
	
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioDao.findByUsername(username);
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.collect(Collectors.toList());
		
		return new User(username, usuario.getPassword(), usuario.getEnable(), true,true,true,authorities);
	}


	@Override
	public Usuario findByUsername(String username) {
		return usuarioDao.findByUsername(username);
	}

}
