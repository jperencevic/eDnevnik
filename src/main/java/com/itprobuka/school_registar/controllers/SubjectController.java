package com.itprobuka.school_registar.controllers;

// CRUD (getAll, getById, addSUbject, updateSubject, deleteSUbject)
// greateGradeSubject

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.dto.CreateGradeSubjectDTO;
import com.itprobuka.school_registar.entities.dto.SubjectTitleDTO;
import com.itprobuka.school_registar.repositories.SubjectRepository;
import com.itprobuka.school_registar.services.SubjectDao;

@RestController
@RequestMapping(path = "sr/subjects")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class SubjectController {

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	SubjectDao subjectDao;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// =======================CRUD=====================================

	// =================getAll====================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<SubjectEntity> result = subjectRepo.findAll();
			return new ResponseEntity<Iterable<SubjectEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =================getById===================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (subjectRepo.existsById(id)) {
				return new ResponseEntity<SubjectEntity>(subjectRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==============addSubject===================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addSubject(@Valid @RequestBody SubjectTitleDTO newSubject, BindingResult result) {
		SubjectEntity subject = new SubjectEntity();

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		if (subjectRepo.findByNameIgnoreCase(newSubject.getTitle()).isPresent()) {
			return new ResponseEntity<String>("Subject already exists in database.", HttpStatus.NOT_ACCEPTABLE);
		}

		try {
			subject.setName(newSubject.getTitle());
			subjectRepo.save(subject);
			logger.error("Error occurred while adding new subject");
			logger.info("Admin (email: " + AuthController.getEmail() + ")  added new subject " + subject);
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============updateSubject================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "update/{ids}")
	public ResponseEntity<?> updateSubject(@PathVariable String ids, @Valid @RequestBody SubjectTitleDTO newSubject,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		try {
			Integer id = Integer.valueOf(ids);
			if (subjectRepo.existsById(id)) {
				SubjectEntity subject = subjectRepo.findById(id).get();
				SubjectEntity oldsubject = subjectRepo.findById(id).get();
				subject.setName(newSubject.getTitle());
				subjectRepo.save(subject);
				logger.error("Error occurred whil updating subject " + subject);
				logger.info("Admin (email: " + AuthController.getEmail() + ") updated subject from:" + oldsubject
						+ " to: " + subject);
				return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============delete subject===============
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeSubject(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (subjectRepo.existsById(id)) {
				SubjectEntity subject = subjectRepo.findById(id).get();
				if (subject.getGradeSubject().isEmpty()) {
					subjectRepo.deleteById(id);
					logger.error("Error occurred while deleting subject");
					logger.info("Admin (email: " + AuthController.getEmail() + ") deleted subject " + subject);
					return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
				} else {
					return new ResponseEntity<RestError>(
							new RestError(10, "can not be deleted it has schoolclasses connected"),
							HttpStatus.BAD_REQUEST);
				}

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ====================end of CRUD====================================

	// =================create GRADE_SUBJECT==================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/grade/{ids}")
	public ResponseEntity<?> createGradeSubject(@PathVariable String ids, @RequestBody CreateGradeSubjectDTO gradSub) {
		Integer id = Integer.valueOf(ids);
		return subjectDao.addGrade(id, gradSub);
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}
}
