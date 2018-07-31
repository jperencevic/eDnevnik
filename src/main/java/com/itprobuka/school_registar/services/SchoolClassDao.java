package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

public interface SchoolClassDao {

	public ResponseEntity<?> addTeacherGradeSubject(Integer scId, Integer tgsId);

	public ResponseEntity<?> addPupil(Integer scId, Integer pId);
	
	public ResponseEntity<?> addGrade(Integer scId, Integer gId);
}
