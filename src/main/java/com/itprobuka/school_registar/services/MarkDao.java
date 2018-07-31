package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

import com.itprobuka.school_registar.entities.dto.MarkDTO;

public interface MarkDao {

	public ResponseEntity<?> createMark(MarkDTO newMark);

	public ResponseEntity<?> calculateFinal(Integer pId, Integer sId);

	public ResponseEntity<?> makeFinal(Integer pId, Integer sId, Integer suggestion);

	ResponseEntity<?> updateMark(Integer id, MarkDTO updateMark);
}
