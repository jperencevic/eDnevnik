package com.itprobuka.school_registar.controllers;

//CRUD (getAll, getById, addSchoolClass, updateSchoolClass, removeSchoolClass)
//addGradeToClass, addPupilToClass, createClassTeacherGradeSubject, returnListOfPupils

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.itprobuka.school_registar.entities.SchoolClassEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.repositories.SchoolClassRepository;
import com.itprobuka.school_registar.services.SchoolClassDao;

@RestController
@RequestMapping(path = "sr/schoolclass")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class SchoolClassController {

	@Autowired
	SchoolClassRepository schoolClassRepo;

	@Autowired
	SchoolClassDao schoolClassDao;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// =================================CRUD=============================================

	// ==============GET ALL=====================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<SchoolClassEntity> result = schoolClassRepo.findAll();
			return new ResponseEntity<Iterable<SchoolClassEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==============GET BY ID===================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (schoolClassRepo.existsById(id)) {
				return new ResponseEntity<SchoolClassEntity>(schoolClassRepo.findById(id).get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no school class with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==============CREATE new schoolClass object===============
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addSchoolClass(@RequestParam String labelS) {
		try {
			Integer label = Integer.valueOf(labelS);
			SchoolClassEntity schoolClass = new SchoolClassEntity();
			schoolClass.setLabel(label);
			schoolClassRepo.save(schoolClass);
			logger.error("Error ocurred while creating new schoolclass");
			logger.info("Admin (email:" + AuthController.getEmail() + ") added new  schoolClass " + schoolClass);
			return new ResponseEntity<SchoolClassEntity>(schoolClass, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============UPDATE schoolClass object===================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "update/{ids}")
	public ResponseEntity<?> updateSchoolClass(@PathVariable String ids, @RequestParam String labelS) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer label = Integer.valueOf(labelS);
			if (schoolClassRepo.existsById(id)) {
				SchoolClassEntity schoolClass = schoolClassRepo.findById(id).get();
				SchoolClassEntity old = schoolClassRepo.findById(id).get();
				schoolClass.setLabel(label);
				schoolClassRepo.save(schoolClass);
				logger.error("Error ocurred while updating schoolclass #id:" + schoolClass.getId());
				logger.info("Admin (email:" + AuthController.getEmail() + ") updated  schoolClass from" + schoolClass
						+ " to: " + old);
				return new ResponseEntity<SchoolClassEntity>(schoolClass, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============DELETE schoolClass object
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeSchoolClass(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (schoolClassRepo.existsById(id)) {
				SchoolClassEntity schoolClass = schoolClassRepo.findById(id).get();
				if (schoolClass.getCtgs().isEmpty()) {
					if (schoolClass.getPupils().isEmpty()) {
						schoolClassRepo.deleteById(id);
						logger.error("Error ocurred while deleting schoolclass #id:" + schoolClass.getId());
						logger.info(
								"Admin (email:" + AuthController.getEmail() + ") deleted  schoolClass " + schoolClass);
						return new ResponseEntity<SchoolClassEntity>(schoolClass, HttpStatus.OK);
					} else
						return new ResponseEntity<RestError>(
								new RestError(12, "It can not be deleted, there are pupils in schoolclass"),
								HttpStatus.NOT_FOUND);
				} else
					return new ResponseEntity<RestError>(
							new RestError(10, "It can not be deleted, there are ctgs connected to schoolclass"),
							HttpStatus.NOT_FOUND);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (

		Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =====================END OF CRUD ==========================================

	// ================ADD attribute GRADE to schoolClass================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/grade/{ids}")
	public ResponseEntity<?> addGradeToClass(@PathVariable String ids, @RequestParam String gIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer gId = Integer.valueOf(gIds);
			return schoolClassDao.addGrade(id, gId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================ADD PUPIL to shoolClass===========================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/pupil/{ids}")
	public ResponseEntity<?> addPupilToClass(@PathVariable String ids, @RequestParam String pIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer pId = Integer.valueOf(pIds);
			return schoolClassDao.addPupil(id, pId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================CREATE new CLASS-TEACHER-GRADE-SUBJECT object=====
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/tgs/{ids}")
	public ResponseEntity<?> createClassTeacherGradeSubject(@PathVariable String ids, @RequestParam String tgsIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer tgsId = Integer.valueOf(tgsIds);
			return schoolClassDao.addTeacherGradeSubject(id, tgsId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ================GET list of PUPILS================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/listOfPupils/{ids}")
	public ResponseEntity<?> returnListOfPupils(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (schoolClassRepo.existsById(id)) {
				List<UPupilEntity> pupils = schoolClassRepo.findById(id).get().getPupils();
				return new ResponseEntity<List<UPupilEntity>>(pupils, HttpStatus.OK);
			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
