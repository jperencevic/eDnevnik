package com.itprobuka.school_registar.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.UParentEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.enumerations.EUserRole;
import com.itprobuka.school_registar.repositories.UParentRepository;
import com.itprobuka.school_registar.services.UserDao;

// CRUD (getAll, getById, addParent, updateParent, removeParent)
// return list of children (getChildren)
@RestController
@RequestMapping(path = "sr/parents")
@CrossOrigin(origins = "http://localhost:4200")
public class UParentController {

	@Autowired
	UParentRepository parentRepo;

	@Autowired
	UserDao userDao;

	// =======================CRUD=================================================================

	// ============GET ALL===============================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<UParentEntity> result = parentRepo.findAll();
			return new ResponseEntity<Iterable<UParentEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============GET parent by ID======================================
	@Secured({ "ROLE_ADMIN", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PARENT)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			if (parentRepo.existsById(id)) {
				return new ResponseEntity<UParentEntity>(parentRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no parent with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ============ADD parent basic info=================================
	// not needed because of addParentToPupil(..) in UPupilController
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addParent(@Valid @RequestBody UserDTO newParent, BindingResult result) {
		UParentEntity parent = new UParentEntity();
		parent.setRole(EUserRole.ROLE_PARENT);
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			parent.setName(newParent.getName());
			parent.setLastName(newParent.getLastName());
			parent.setEmail(newParent.getEmail());
			parentRepo.save(parent);
			return new ResponseEntity<UParentEntity>(parent, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============UPDATE parents basic info=============================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updateParent(@PathVariable String ids, @Valid @RequestBody UserDTO updateParent,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			if (parentRepo.existsById(id)) {
				UParentEntity parent = parentRepo.findById(id).get();
				parent.setName(updateParent.getName());
				parent.setLastName(updateParent.getLastName());
				parent.setEmail(updateParent.getEmail());
				parentRepo.save(parent);
				return new ResponseEntity<UParentEntity>(parent, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no parent with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============REMOVE parent=========================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeParent(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (parentRepo.existsById(id)) {
				UParentEntity parent = parentRepo.findById(id).get();
				parentRepo.deleteById(id);
				return new ResponseEntity<UParentEntity>(parent, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no parent with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ==========================END OF
	// CRUD=========================================================

	// ==========return list of children=================================
	@Secured({ "ROLE_ADMIN", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.GET, value = "/children/{ids}")
	public ResponseEntity<?> getChildren(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PARENT)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<List<UPupilEntity>>(parentRepo.findById(id).get().getChildren(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ========================UPDATE PASSWORD====================================
	@Secured({ "ROLE_ADMIN", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.PUT, value = "/password/{ids}")
	public ResponseEntity<?> updatePassword(@PathVariable String ids, @RequestParam String oldP,
			@RequestParam String newP) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInUser().getRole().equals(EUserRole.ROLE_PARENT)
					&& !userDao.getLoggedInId().equals(id)) {
				return new ResponseEntity<RestError>(new RestError(10, "Not authorized"), HttpStatus.BAD_REQUEST);
			}
			return userDao.updatePasword(id, oldP, newP);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}
}
