package com.mbaront.cobros.diarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CobrosDiariosApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public static void main(String[] args) {
		SpringApplication.run(CobrosDiariosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password="12345";
		
		String passwordBCrypt = passwordEncoder.encode(password);
		System.out.println("el password " + passwordBCrypt);
	}
	
	

}
