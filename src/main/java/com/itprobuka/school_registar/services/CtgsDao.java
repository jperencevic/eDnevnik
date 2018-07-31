package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;
import com.itprobuka.school_registar.entities.dto.CtgsDTO;

public interface CtgsDao {

	public ResponseEntity<?> putCtgs(Integer ctgsId, CtgsDTO ctgs);

	public ResponseEntity<?> deleteCtgs(Integer id);

}
