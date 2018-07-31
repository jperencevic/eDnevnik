package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

public interface TgsDao {

	ResponseEntity<?> updateTgs(Integer id, Integer tId, Integer gsId);
}
