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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.UTeacherEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.enumerations.EUserRole;
import com.itprobuka.school_registar.repositories.UTeacherRepository;
import com.itprobuka.school_registar.services.TeacherDao;
import com.itprobuka.school_registar.services.UserDao;

// CRUD (getAll, getById, addTeacher, updateTeacher, deleteTeacher)
// add combination grade_subject to teacher (createTeacherGradeSubject)
// createTeacherGradeSubjectLowerGradeTeacher, 
// createTeacherGradeSubjectSubjectTeacher,
// update password
// getCtgsForTeacher

@RestController
@RequestMapping(path = "/sr/teachers")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UTeacherController {

	@Autowired
	UTeacherRepository teacherRepo;

	@Autowired
	TeacherDao teacherDao;

	@Autowired
	UserDao userDao;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// ======CRUD=============================================================================

	// =============GET ALL teachers=========================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {

		try {
			Iterable<UTeacherEntity> result = teacherRepo.findAll();
			return new ResponseEntity<Iterable<UTeacherEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============GET teacher by ID=======================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_TEACHER)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			if (teacherRepo.existsById(id)) {
				return new ResponseEntity<UTeacherEntity>(teacherRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===============POST new teacher - basic information===================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addTeacher(@Valid @RequestBody UserDTO newTeacher, BindingResult result) {
		UTeacherEntity teacher = new UTeacherEntity();
		teacher.setRole(EUserRole.ROLE_TEACHER);
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			return teacherDao.postPutTeacher(teacher, newTeacher);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===========UPDATE teacher's basic information ========================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updateTeacher(@PathVariable String ids, @Valid @RequestBody UserDTO updateTeacher,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			if (teacherRepo.existsById(id)) {
				UTeacherEntity teacher = teacherRepo.findById(id).get();
				return teacherDao.postPutTeacher(teacher, updateTeacher);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============DELETE teacher============================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeTeacher(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (teacherRepo.existsById(id)) {
				UTeacherEntity teacher = teacherRepo.findById(id).get();
				if (teacher.getTgs().isEmpty()) {
					teacherRepo.deleteById(id);
					logger.error("Error occured while deleting teacher #id" + teacher.getId());
					logger.info("Admin (email: " + AuthController.getEmail() + ") deleted subject " + teacher);
					return new ResponseEntity<UTeacherEntity>(teacher, HttpStatus.OK);
				} else {
					teacher.setRole(EUserRole.NOT_ACTIVE);
					teacherDao.setNotActiveCtgs(teacher);
					return new ResponseEntity<UTeacherEntity>(teacher, HttpStatus.OK);
				}

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =============================end of
	// CRUD===============================================

	// ================UPDATE teacher, add information about subjects=========
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/gradeSubject/{ids}")
	public ResponseEntity<?> createTeacherGradeSubject(@PathVariable String ids, @RequestParam String gsIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer gsId = Integer.valueOf(gsIds);
			return teacherDao.addGradeSubject(id, gsId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================UPDATE teacher, create lower grade teacher=========
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/gradeSubjectLowerTeacher/{ids}")
	public ResponseEntity<?> createTeacherGradeSubjectLowerGrade(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);

			return teacherDao.lowerGradeTeacher(id);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================UPDATE teacher, create subject teacher=========
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/gradeSubjectSubjectTeacher/{ids}")
	public ResponseEntity<?> createTeacherGradeSubjectSubjectTeacher(@PathVariable String ids,
			@RequestParam String sIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer sId = Integer.valueOf(sIds);
			return teacherDao.subjectTeacher(id, sId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ========================UPDATE PASSWORD====================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.PUT, value = "/password/{ids}")
	public ResponseEntity<?> updatePassword(@PathVariable String ids, @RequestParam String oldP,
			@RequestParam String newP) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_TEACHER)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			return userDao.updatePasword(id, oldP, newP);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ======================GET CTGS connected with specific teacher=============
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/getCTGS/{ids}")
	public ResponseEntity<?> getCtgsForTeacher(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_TEACHER)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}

			return teacherDao.getCTGS(id);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured({ "ROLE_TEACHER", "ROLE_ADMIN" })
	@RequestMapping(method = RequestMethod.GET, value = "/studMarks/{tIds}/{ctgsIds}")
	public ResponseEntity<?> getStudentsMarks(@PathVariable String tIds, @PathVariable String ctgsIds) {
		try {
			Integer tId = Integer.valueOf(tIds);
			Integer ctgsId = Integer.valueOf(ctgsIds);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_TEACHER)
					&& !userDao.getLoggedInId().equals(tId)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			return teacherDao.getStudentsMarks(tId, ctgsId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

}
