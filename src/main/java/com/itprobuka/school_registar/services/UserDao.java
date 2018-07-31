package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

import com.itprobuka.school_registar.entities.UserEntity;

public interface UserDao {

	public ResponseEntity<?> updatePasword(Integer id, String oldP, String newP);

	public Integer getLoggedInId();

	public UserEntity getLoggedInUser();
}
