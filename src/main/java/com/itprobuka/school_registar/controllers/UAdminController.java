package com.itprobuka.school_registar.controllers;

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
import com.itprobuka.school_registar.entities.UAdminEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.enumerations.EUserRole;
import com.itprobuka.school_registar.repositories.UAdminRepository;
import com.itprobuka.school_registar.services.UserDao;

@RestController
@RequestMapping(path = "sr/admins")
@CrossOrigin(origins = "http://localhost:4200")
public class UAdminController {

	@Autowired
	UAdminRepository adminRepo;

	@Autowired
	UserDao userDao;

	// =====================CRUD======================================================================

	// ============GET ALL==================================

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<UAdminEntity> result = adminRepo.findAll();
			return new ResponseEntity<Iterable<UAdminEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============GET admin by ID==========================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (adminRepo.existsById(id)) {
				return new ResponseEntity<UAdminEntity>(adminRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no admin with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ============ADD admin basic info=====================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addAdmin(@Valid @RequestBody UserDTO newAdmin, BindingResult result) {
		UAdminEntity admin = new UAdminEntity();
		admin.setRole(EUserRole.ROLE_ADMIN);
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			admin.setName(newAdmin.getName());
			admin.setLastName(newAdmin.getLastName());
			admin.setEmail(newAdmin.getEmail());
			adminRepo.save(admin);
			return new ResponseEntity<UAdminEntity>(admin, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============UPDATE admins basic info=================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updateAdmin(@PathVariable String ids, @Valid @RequestBody UserDTO updateAdmin,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			Integer id = Integer.valueOf(ids);
			if (adminRepo.existsById(id)) {
				UAdminEntity admin = adminRepo.findById(id).get();
				admin.setName(updateAdmin.getName());
				admin.setLastName(updateAdmin.getLastName());
				admin.setEmail(updateAdmin.getEmail());
				adminRepo.save(admin);
				return new ResponseEntity<UAdminEntity>(admin, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no admin with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============REMOVE admin=============================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeAdmin(@PathVariable String ids) {
		try {
			Integer id = Integer.valueOf(ids);
			if (adminRepo.existsById(id)) {
				UAdminEntity admin = adminRepo.findById(id).get();
				adminRepo.deleteById(id);
				return new ResponseEntity<UAdminEntity>(admin, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no admin with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ======================END OF
	// CRUD===============================================================

	// ========================UPDATE PASSWORD====================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/password/{ids}")
	public ResponseEntity<?> updatePassword(@PathVariable String ids, @RequestParam String oldP,
			@RequestParam String newP) {
		try {
			Integer id = Integer.valueOf(ids);
			if (userDao.getLoggedInId().equals(id)) {
				return userDao.updatePasword(id, oldP, newP);
			} else {
				return new ResponseEntity<RestError>(new RestError(101, "You can only change your password"),
						HttpStatus.UNAUTHORIZED);
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
