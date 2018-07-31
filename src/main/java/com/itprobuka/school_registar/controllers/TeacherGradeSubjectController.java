package com.itprobuka.school_registar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;
import com.itprobuka.school_registar.repositories.Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.services.TgsDao;

@RestController
@RequestMapping(path = "/sr/tgs")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TeacherGradeSubjectController {

	@Autowired
	Teacher_Grade_SubjectRepository teacherGradeSubjectRepo;

	@Autowired
	TgsDao tgsDao;

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<Teacher_Grade_Subject> result = teacherGradeSubjectRepo.findAll();
			return new ResponseEntity<Iterable<Teacher_Grade_Subject>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		Integer id = Integer.valueOf(ids);

		try {
			if (teacherGradeSubjectRepo.existsById(id)) {
				return new ResponseEntity<Teacher_Grade_Subject>(teacherGradeSubjectRepo.findById(id).get(),
						HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(
						new RestError(10, "There is no teacher_grade_subject with such ID"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "update/{ids}")
	public ResponseEntity<?> updateTeacherGradeSubject(@PathVariable String ids, @RequestParam String tIds,
			@RequestParam String gsIds) {

		try {
			Integer id = Integer.valueOf(ids);
			Integer tId = Integer.valueOf(tIds);
			Integer gsId = Integer.valueOf(gsIds);

			return tgsDao.updateTgs(id, tId, gsId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeTeacherGradeSubject(@PathVariable String ids) {

		try {
			Integer id = Integer.valueOf(ids);
			if (teacherGradeSubjectRepo.existsById(id)) {
				Teacher_Grade_Subject gs = teacherGradeSubjectRepo.findById(id).get();
				teacherGradeSubjectRepo.deleteById(id);
				return new ResponseEntity<Teacher_Grade_Subject>(gs, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(
						new RestError(10, "There is no teacher_grade_subject with such ID"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
