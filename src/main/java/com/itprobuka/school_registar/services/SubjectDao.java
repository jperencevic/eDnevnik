package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

import com.itprobuka.school_registar.entities.dto.CreateGradeSubjectDTO;

public interface SubjectDao {

	public ResponseEntity<?> addGrade(Integer sub, CreateGradeSubjectDTO graSub);
}
