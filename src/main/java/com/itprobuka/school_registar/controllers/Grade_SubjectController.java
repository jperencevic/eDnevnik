package com.itprobuka.school_registar.controllers;

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
import com.itprobuka.school_registar.entities.Grade_Subject;
import com.itprobuka.school_registar.entities.dto.GradeSubjectDTO;
import com.itprobuka.school_registar.repositories.Grade_SubjectRepository;

//CRUD (getAll, getById, updateGradeSubject, removeGradeSubject)

@RestController
@RequestMapping(path = "/sr/grade_subject")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class Grade_SubjectController {

	@Autowired
	Grade_SubjectRepository gradeSubjectRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// ===================CRUD======================================
	// ===========getAll==================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<Grade_Subject> result = gradeSubjectRepo.findAll();
			return new ResponseEntity<Iterable<Grade_Subject>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===========getById=================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (gradeSubjectRepo.existsById(id)) {
				return new ResponseEntity<Grade_Subject>(gradeSubjectRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no grade_subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===========update==================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "update/{ids}")
	public ResponseEntity<?> updateGradeSubject(@PathVariable String ids,
			@Valid @RequestBody GradeSubjectDTO updateGradeSubject, BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			if (gradeSubjectRepo.existsById(id)) {
				Grade_Subject gs = gradeSubjectRepo.findById(id).get();
				Grade_Subject oldgs = gradeSubjectRepo.findById(id).get();
				gs.setActive(updateGradeSubject.getActive());
				gs.setBook(updateGradeSubject.getBook());
				gs.setHoursPerWeek(updateGradeSubject.getHoursPerWeek());
				gs.setWorkBook(updateGradeSubject.getWorkBook());
				gs.setOtherLiterature(updateGradeSubject.getOtherLiterature());
				gradeSubjectRepo.save(gs);
				logger.error("Error occurred");
				logger.info("Admin (email: " + AuthController.getEmail() + ") updated gs from " + oldgs + " to " + gs);
				return new ResponseEntity<Grade_Subject>(gs, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no grade_subject with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===========delete==================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeGradeSubject(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (gradeSubjectRepo.existsById(id)) {
				Grade_Subject gs = gradeSubjectRepo.findById(id).get();
				if (gs.getTgs().isEmpty()) {
					gradeSubjectRepo.deleteById(id);
					logger.error("Error occurred");
					logger.info("Admin (email: " + AuthController.getEmail() + ") deleted gs: " + gs);
					return new ResponseEntity<Grade_Subject>(gs, HttpStatus.OK);
				} else {
					gs.setActive(false);
					gradeSubjectRepo.save(gs);
					logger.error("Error occurred");
					logger.info(
							"Admin (email: " + AuthController.getEmail() + ") gs, id " + gs.getId() + "to not activeF");
					return new ResponseEntity<RestError>(
							new RestError(10,
									"Impossible to delete, it has connections to tgs, it's set to not active"),
							HttpStatus.OK);
				}
			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no grade_subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}
}
