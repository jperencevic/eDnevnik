package com.itprobuka.school_registar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.repositories.UserRepository;

@RestController
@RequestMapping(path = "sr/login")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

	@Autowired
	UserRepository userRepo;

	@PostMapping
	public ResponseEntity<Object> login() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = auth.getAuthorities().iterator().next().getAuthority();
		String email = auth.getName();
		String id = "" + userRepo.findByEmail(email).getId();
		return new ResponseEntity<>("{\"id\":\"" + id + "\",\"role\":\"" + role + "\"}", HttpStatus.OK);
	}

	public static String getEmail() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return email;
	}

}
