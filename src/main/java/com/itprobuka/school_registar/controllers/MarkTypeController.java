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
import com.itprobuka.school_registar.entities.MarkTypeEntity;
import com.itprobuka.school_registar.entities.dto.MarkTypeDTO;
import com.itprobuka.school_registar.repositories.MarkTypeRepository;

@RestController
@RequestMapping(path = "/sr/marktype")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class MarkTypeController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	MarkTypeRepository markTypeRepo;

	// ======CRUD=============================================================================

	// =============GET ALL mark types=========================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<MarkTypeEntity> result = markTypeRepo.findAll();
			return new ResponseEntity<Iterable<MarkTypeEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============GET mark type by ID=======================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {
		Integer id = Integer.valueOf(ids);

		try {
			if (markTypeRepo.existsById(id)) {
				return new ResponseEntity<MarkTypeEntity>(markTypeRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no mark type with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===============POST new mark type=====================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addMarkType(@Valid @RequestBody MarkTypeDTO newMarkType, BindingResult result) {
		MarkTypeEntity markType = new MarkTypeEntity();

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			markType.setType(newMarkType.getType());
			markType.setDescription(newMarkType.getDescription());
			markTypeRepo.save(markType);
			logger.error("Error occurres while creating new mark type");
			logger.info("Admin (email: " + AuthController.getEmail() + ") created new mark type " + markType);
			return new ResponseEntity<MarkTypeEntity>(markType, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===========UPDATE mark type===========================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updateMarkType(@PathVariable String ids, @Valid @RequestBody MarkTypeDTO updateMarkType,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		Integer id = Integer.valueOf(ids);
		try {

			if (markTypeRepo.existsById(id)) {
				MarkTypeEntity markType = markTypeRepo.findById(id).get();
				MarkTypeEntity oldmarkType = markTypeRepo.findById(id).get();
				markType.setType(updateMarkType.getType());
				markType.setDescription(updateMarkType.getDescription());
				markTypeRepo.save(markType);
				logger.error("Error occurred while updating markType");
				logger.info("Admin (email: " + AuthController.getEmail() + ") updated mark type from:" + oldmarkType
						+ " to: " + markType);
				return new ResponseEntity<MarkTypeEntity>(markType, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no mark type with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============DELETE marktype============================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeMarkType(@PathVariable String ids) {
		Integer id = Integer.valueOf(ids);

		try {
			if (markTypeRepo.existsById(id)) {
				MarkTypeEntity markType = markTypeRepo.findById(id).get();
				if (markType.getMarks().isEmpty()) {
					markTypeRepo.deleteById(id);
					logger.error("Error occurred while deleting mark type");
					logger.info("Admin (email: " + AuthController.getEmail() + ") deleted mark type " + markType);
					return new ResponseEntity<MarkTypeEntity>(markType, HttpStatus.OK);
				} else {
					return new ResponseEntity<RestError>(new RestError(10, "Can not delete mark type it has marks "),
							HttpStatus.NOT_FOUND);
				}

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no markType with such ID"),
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
