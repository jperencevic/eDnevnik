package com.itprobuka.school_registar.controllers;

import java.util.Optional;
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
import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.MarkDTO;
import com.itprobuka.school_registar.repositories.MarkRepository;
import com.itprobuka.school_registar.repositories.SubjectRepository;
import com.itprobuka.school_registar.repositories.UPupilRepository;
import com.itprobuka.school_registar.services.MarkDao;

//CRUD (getAll, betById, addMark, updateMark, removeMark)
//calculateFinalMark, makeFinalMark

@RestController
@RequestMapping(path = "/sr/marks")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class MarkController {

	private MarkRepository markRepo;

	private MarkDao markDao;

	private UPupilRepository pupilRepo;
	private SubjectRepository subjectRepo;

	@Autowired
	public MarkController(UPupilRepository pupilRepository, SubjectRepository subjectRepo, MarkRepository markRepo,
			MarkDao markDao) {
		this.pupilRepo = pupilRepository;
		this.subjectRepo = subjectRepo;
		this.markDao = markDao;
		this.markRepo = markRepo;
	}

	// @Autowired
	// SubjectRepository subjectRepo;

	// ======CRUD=============================================================================

	// =============GET ALL marks=========================================
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		try {
			Iterable<MarkEntity> result = markRepo.findAll();
			return new ResponseEntity<Iterable<MarkEntity>>(result, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ==============GET mark by ID=======================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/by_id/{ids}")
	public ResponseEntity<?> getById(@PathVariable String ids) {

		try {
			Integer id = Integer.valueOf(ids);
			if (markRepo.existsById(id)) {
				return new ResponseEntity<MarkEntity>(markRepo.findById(id).get(), HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no mark with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ===============POST new mark=======================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addMark(@Valid @RequestBody MarkDTO newMark, BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			return markDao.createMark(newMark);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ===========UPDATE mark=================================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{ids}")
	public ResponseEntity<?> updateMark(@PathVariable String ids, @Valid @RequestBody MarkDTO updateMark,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {

			Integer id = Integer.valueOf(ids);
			if (markRepo.existsById(id)) {
				return markDao.updateMark(id, updateMark);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no mark with such ID"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ============DELETE mark============================================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{ids}")
	public ResponseEntity<?> removeMark(@PathVariable String ids) {

		try {
			Integer id = Integer.valueOf(ids);
			if (markRepo.existsById(id)) {
				MarkEntity mark = markRepo.findById(id).get();
				markRepo.deleteById(id);
				return new ResponseEntity<MarkEntity>(mark, HttpStatus.OK);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no mark with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =============================end of
	// CRUD===============================================

	// =========================CALCULATE FINAL MARK /MAKE A
	// SUGGESTION/==============
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/calculate_final/{pIds}/subj/{sIds}")
	private ResponseEntity<?> calculateFinalMark(@PathVariable String pIds, @PathVariable String sIds) {

		try {

			Iterable<UPupilEntity> pupils = getAllPupile();
			UPupilEntity pupil = getPupil();
			SubjectEntity subject = subjectRepo.findById(1).get();

			// if (!markRepo.findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(pupil,
			// subject, "zakljucna")
			// .isPresent()) {
			// List<MarkEntity> marks =
			// markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(pupil, subject);
			// Double sum = 0.0;
			// for (MarkEntity mark : marks) {
			// sum += mark.getMark();
			// }
			//
			// Integer finalMark = (int) Math.round(sum / marks.size());
			// return new ResponseEntity<Integer>(finalMark, HttpStatus.OK);
			// } else {
			return new ResponseEntity<RestError>(new RestError(9, "There is already final mark!"),
					HttpStatus.BAD_REQUEST);
			// }
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	// =========================MAKE FINAL MARK, BASED ON
	// SUGGESTION==================
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.POST, value = "make_final/{pIds}/subj/{sIds}")
	private ResponseEntity<?> makeFinalMark(@PathVariable String pIds, @PathVariable String sIds,
			@RequestParam String sugg) {

		try {
			Integer pId = Integer.valueOf(pIds);
			Integer sId = Integer.valueOf(sIds);
			Integer suggestion = Integer.parseInt(sugg);
			return markDao.makeFinal(pId, sId, suggestion);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	private UPupilEntity getPupil() {
		Integer id = 17;
		return pupilRepo.findById(id).get();
	}

	private Iterable<UPupilEntity> getAllPupile() {
		return pupilRepo.findAll();
	}
}
