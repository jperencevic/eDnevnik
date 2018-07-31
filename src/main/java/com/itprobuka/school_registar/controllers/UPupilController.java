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
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.enumerations.EUserRole;
import com.itprobuka.school_registar.repositories.UPupilRepository;
import com.itprobuka.school_registar.services.PupilDao;
import com.itprobuka.school_registar.services.UserDao;

// CRUD (getAll, getById, addPupil, updatePupil, removePupil)
// add parent and add schoolclass to pupil (addParentToPupil, addSchoolClassToPupil)
@RestController
@RequestMapping(path = "sr/pupils")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UPupilController {

	@Autowired
	UPupilRepository pupilRepo;

	@Autowired
	PupilDao pupilDao;

	@Autowired
	UserDao userDao;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// =======================CRUD================================================================

	// ===============GET ALL==================================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<UPupilEntity> result = pupilRepo.findAll();
			return new ResponseEntity<Iterable<UPupilEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===============GET pupil by ID==========================================
	@Secured({ "ROLE_ADMIN", "ROLE_PUPIL" })
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PUPIL)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			if (pupilRepo.existsById(id)) {
				return new ResponseEntity<UPupilEntity>(pupilRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===============POST new pupil basic info================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addPupil(@Valid @RequestBody UserDTO newPupil, BindingResult result) {
		UPupilEntity pupil = new UPupilEntity();
		pupil.setRole(EUserRole.ROLE_PUPIL);
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			return pupilDao.postPutPupil(pupil, newPupil);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===============UPDATE pupils basic info=================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updatePupil(@PathVariable String ids, @Valid @RequestBody UserDTO updatePupil,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			if (pupilRepo.existsById(id)) {
				UPupilEntity pupil = pupilRepo.findById(id).get();
				return pupilDao.postPutPupil(pupil, updatePupil);
			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===============DELETE pupil=============================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removePupil(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (pupilRepo.existsById(id)) {
				UPupilEntity pupil = pupilRepo.findById(id).get();
				pupilDao.removeParent(pupil);
				if (pupil.getMarks().isEmpty()) {
					pupilRepo.deleteById(id);
					logger.error("Error occurred while deleting pupil: " + pupil);
					logger.info("Admin (email: " + AuthController.getEmail() + ")  deleted pupil " + pupil);
					return new ResponseEntity<UPupilEntity>(pupil, HttpStatus.OK);
				} else {
					logger.error("Error occurred while deleting pupil: " + pupil);
					logger.info("Admin (email: " + AuthController.getEmail() + ")  set NOT_ACTIVE to: " + pupil);
					pupil.setRole(EUserRole.NOT_ACTIVE);
					return new ResponseEntity<RestError>(
							new RestError(10, "Can not delete pupil it has marks, it is set to NOT_ACTIVE"),
							HttpStatus.NOT_FOUND);
				}
			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==============================END OF
	// CRUD====================================================

	// ===============UPDATE pupil-add parent==================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/addParent/{ids}")
	public ResponseEntity<?> addParentToPupil(@PathVariable String ids, @RequestBody UserDTO parent,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		try {
			Integer id = Integer.valueOf(ids);
			if (pupilRepo.existsById(id)) {
				UPupilEntity pupil = pupilRepo.findById(id).get();
				pupilDao.addParent(pupil, parent);
				return new ResponseEntity<UPupilEntity>(pupil, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===============UPDATE pupil add schoolclass=============================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/addSchoolClass/{ids}/{scIds}")
	public ResponseEntity<?> addSchoolClassToPupil(@PathVariable String ids, @PathVariable String scIds) {
		try {
			Integer id = Integer.valueOf(ids);
			Integer scId = Integer.valueOf(scIds);
			if (pupilRepo.existsById(id)) {
				UPupilEntity pupil = pupilRepo.findById(id).get();
				return pupilDao.addSchoolClass(pupil, scId);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ========================UPDATE PASSWORD====================================
	@Secured({ "ROLE_ADMIN", "ROLE_PUPIL" })
	@RequestMapping(method = RequestMethod.PUT, value = "/password/{ids}")
	public ResponseEntity<?> updatePassword(@PathVariable String ids, @RequestParam String oldP,
			@RequestParam String newP) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PUPIL)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			return userDao.updatePasword(id, oldP, newP);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Secured({ "ROLE_ADMIN", "ROLE_PUPIL", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.GET, value = "getMarksSubjects/{pIds}")
	public ResponseEntity<?> getMarksSubjects(@PathVariable String pIds) {
		try {
			Integer id = Integer.valueOf(pIds);
			if ((userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PUPIL) && userDao.getLoggedInId().equals(id))
					|| userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PARENT)
							&& pupilDao.isItAParrent(id, userDao.getLoggedInUser().getId())) {

				return pupilDao.getMarksSubjects(id);
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}
}
