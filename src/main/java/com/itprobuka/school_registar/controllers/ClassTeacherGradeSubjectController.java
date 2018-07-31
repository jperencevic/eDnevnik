package com.itprobuka.school_registar.controllers;

// CRUD (getAll, getById, updateCtgs, deleteCtga)
// returnPupilsConnected

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.CtgsDTO;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.services.CtgsDao;

@RestController
@RequestMapping(path = "sr/ctgs")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ClassTeacherGradeSubjectController {

	@Autowired
	Class_Teacher_Grade_SubjectRepository classTeacherGradeSubjectRepo;

	@Autowired
	CtgsDao ctgsDao;

	// ======================CRUD===================================================
	// ============GET All======================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<Class_Teacher_Grade_Subject> result = classTeacherGradeSubjectRepo.findAll();
			return new ResponseEntity<Iterable<Class_Teacher_Grade_Subject>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =================getById=====================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (classTeacherGradeSubjectRepo.existsById(id)) {
				return new ResponseEntity<Class_Teacher_Grade_Subject>(classTeacherGradeSubjectRepo.findById(id).get(),
						HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(
						new RestError(10, "There is no class_teacher_grade_subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==================Update CTGS================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "update/{ids}")
	public ResponseEntity<?> updateClassTeacherGradeSubject(@PathVariable String ids, @Valid @RequestBody CtgsDTO ctgs,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			return ctgsDao.putCtgs(id, ctgs);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================DELETE ctgs==============
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeClassTeacherGradeSubject(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			return ctgsDao.deleteCtgs(id);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// =============END of CRUD============================================

	// ============get pupils connected to CTGS================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/pupilsInClass/{ids}")
	public ResponseEntity<?> returnPupilsConnected(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (classTeacherGradeSubjectRepo.existsById(id)) {
				Class_Teacher_Grade_Subject ctgs = classTeacherGradeSubjectRepo.findById(id).get();
				List<UPupilEntity> pupils = ctgs.getSchoolClass().getPupils();
				return new ResponseEntity<List<UPupilEntity>>(pupils, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(
						new RestError(10, "There is no class_teacher_grade_subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(""));
	}

}
