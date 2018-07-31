package com.itprobuka.school_registar.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.AuthController;
import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Grade_Subject;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.dto.CreateGradeSubjectDTO;
import com.itprobuka.school_registar.repositories.GradeRepository;
import com.itprobuka.school_registar.repositories.Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.SubjectRepository;

@Service
public class SubjectDaoImpl implements SubjectDao {

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	GradeRepository gradeRepo;

	@Autowired
	Grade_SubjectRepository gradeSubjectRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Override
	public ResponseEntity<?> addGrade(Integer sub, CreateGradeSubjectDTO graSub) {
		try {
			if (subjectRepo.existsById(sub)) {
				SubjectEntity subject = subjectRepo.findById(sub).get();
				if (gradeRepo.existsById(graSub.getGraId())) {
					if (gradeSubjectRepo.findByGrade_idAndSubject_id(graSub.getGraId(), sub).isPresent()
							&& gradeSubjectRepo.findByGrade_idAndSubject_id(graSub.getGraId(), sub).get().getActive()) {
						return new ResponseEntity<String>("Already exists in database.", HttpStatus.NOT_ACCEPTABLE);
					} else {
						Grade_Subject gs = new Grade_Subject();
						gs.setGrade(gradeRepo.findById(graSub.getGraId()).get());
						gs.setSubject(subject);
						gs.setHoursPerWeek(graSub.getHoursPerWeek());
						gs.setActive(graSub.getActive());
						gs.setBook(graSub.getBook());
						gs.setWorkBook(graSub.getWorkBook());
						gs.setOtherLiterature(graSub.getOtherLiterature());
						gradeSubjectRepo.save(gs);
						logger.error("Error occurred while adding new grade subject");
						logger.info("Admin (email: " + AuthController.getEmail() + ") added new grade_subject " + gs);
						return new ResponseEntity<Grade_Subject>(gs, HttpStatus.OK);
					}
				} else
					return new ResponseEntity<RestError>(new RestError(10, "There is no grade with such ID"),
							HttpStatus.NOT_FOUND);

			} else
				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
						HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
