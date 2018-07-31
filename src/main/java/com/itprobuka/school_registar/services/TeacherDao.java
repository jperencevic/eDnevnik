package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

import com.itprobuka.school_registar.entities.UTeacherEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;

public interface TeacherDao {

	ResponseEntity<?> addGradeSubject(Integer teacher, Integer gradSub);

	ResponseEntity<?> lowerGradeTeacher(Integer tId);

	ResponseEntity<?> subjectTeacher(Integer id, Integer sId);

	ResponseEntity<?> postPutTeacher(UTeacherEntity teacher, UserDTO newTeacher);

	void setNotActiveCtgs(UTeacherEntity teacher);

	ResponseEntity<?> getCTGS(Integer tId);

	ResponseEntity<?> getStudentsMarks(Integer tId, Integer ctgsId);

}
