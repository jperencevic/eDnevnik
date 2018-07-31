package com.itprobuka.school_registar.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.AuthController;
import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.SchoolClassEntity;
import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.dto.CtgsDTO;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.SchoolClassRepository;
import com.itprobuka.school_registar.repositories.Teacher_Grade_SubjectRepository;

@Service
public class CtgsDaoImpl implements CtgsDao {

	@Autowired
	Class_Teacher_Grade_SubjectRepository ctgsRepo;

	@Autowired
	SchoolClassRepository schoolclassRepo;

	@Autowired
	Teacher_Grade_SubjectRepository tgsRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Override
	public ResponseEntity<?> putCtgs(Integer id, CtgsDTO ctgs) {
		if (ctgsRepo.existsById(id)) {
			if (schoolclassRepo.existsById(ctgs.getSchoolClassId())) {
				if (tgsRepo.existsById(ctgs.getTgsId())) {
					SchoolClassEntity sc = schoolclassRepo.findById(ctgs.getSchoolClassId()).get();
					Teacher_Grade_Subject tgs = tgsRepo.findById(ctgs.getTgsId()).get();
					if (sc.getGrade().equals(tgs.getGradeSubject().getGrade())) {
						Class_Teacher_Grade_Subject ctgs2b = ctgsRepo.findById(id).get();
						Class_Teacher_Grade_Subject oldctgs = ctgsRepo.findById(id).get();
						ctgs2b.setActive(ctgs.getActive());
						ctgs2b.setSchoolClass(sc);
						ctgs2b.setTgs(tgs);
						ctgsRepo.save(ctgs2b);
						logger.error("Error occurred while updating ctgs");
						logger.info("Admin (email:" + AuthController.getEmail() + ") updated  ctgs from:{ id:"
								+ oldctgs.getId() + ", schoolClass_id: " + oldctgs.getSchoolClass().getId()
								+ ", tgs_id:	 " + oldctgs.getTgs().getId() + " } to:{ id:" + ctgs2b.getId()
								+ ", schoolClass_id: " + ctgs2b.getSchoolClass().getId() + ", tgs_id:	 "
								+ ctgs2b.getTgs().getId() + " }");
						return new ResponseEntity<Class_Teacher_Grade_Subject>(ctgs2b, HttpStatus.OK);
					} else {
						return new ResponseEntity<RestError>(new RestError(5,
								"Provided competance (teacher_grade_subject) grade does not match grade of schoolclass"),
								HttpStatus.BAD_REQUEST);
					}
				} else {
					return new ResponseEntity<RestError>(new RestError(10, "There is no tgs with such ID"),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no stgs with such ID"),
					HttpStatus.NOT_FOUND);
		}

	}

	@Override
	public ResponseEntity<?> deleteCtgs(Integer id) {
		if (ctgsRepo.existsById(id)) {
			Class_Teacher_Grade_Subject ctgs = ctgsRepo.findById(id).get();
			if (ctgs.getMarks().isEmpty()) {
				ctgsRepo.deleteById(id);
				logger.error("Error occurred");
				logger.info("Admin (email:" + AuthController.getEmail() + ") deleted  ctgs:{ id:" + ctgs.getId()
						+ ", schoolClass_id: " + ctgs.getSchoolClass().getId() + ", tgs_id:	 " + ctgs.getTgs().getId()
						+ " }");
				return new ResponseEntity<Class_Teacher_Grade_Subject>(ctgs, HttpStatus.OK);
			} else {
				ctgs.setActive(false);
				logger.error("Error occurred while deleting ctgs");
				logger.info("Admin (email:" + AuthController.getEmail() + ") updated ctgs with id:" + ctgs.getId()
						+ " active = false ");
				return new ResponseEntity<RestError>(
						new RestError(10, "Imposible to delete, it has marks connected, it's set to not active"),
						HttpStatus.OK);
			}

		} else
			return new ResponseEntity<RestError>(
					new RestError(10, "There is no class_teacher_grade_subject with such ID"), HttpStatus.NOT_FOUND);

	}

}
