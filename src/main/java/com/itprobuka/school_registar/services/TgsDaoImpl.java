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
import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.UTeacherEntity;
import com.itprobuka.school_registar.enumerations.ETitle;
import com.itprobuka.school_registar.repositories.Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.UTeacherRepository;

@Service
public class TgsDaoImpl implements TgsDao {

	@Autowired
	UTeacherRepository teacherRepo;

	@Autowired
	Grade_SubjectRepository gradeSubjectRepo;

	@Autowired
	Teacher_Grade_SubjectRepository tgsRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Override
	public ResponseEntity<?> updateTgs(Integer id, Integer tId, Integer gsId) {
		if (tgsRepo.existsById(id)) {
			if (teacherRepo.existsById(tId)) {
				if (gradeSubjectRepo.existsById(gsId)) {
					UTeacherEntity teacher = teacherRepo.findById(tId).get();
					Grade_Subject gradSub = gradeSubjectRepo.findById(gsId).get();
					Integer label = gradSub.getGrade().getLabel();

					if (tgsRepo.findByTeacher_idAndGradeSubject_id(tId, gsId).isPresent()) {
						return new ResponseEntity<String>("Already exists in database.", HttpStatus.NOT_ACCEPTABLE);
					} else {
						if ((teacher.getTitle() == ETitle.SUBJECT_TEACHER && label > 4)
								|| (teacher.getTitle() == ETitle.LOWER_GRADES_TEACHER && label < 5)) {
							Teacher_Grade_Subject tgs = tgsRepo.findById(id).get();
							Teacher_Grade_Subject oldtgs = tgsRepo.findById(id).get();
							tgs.setTeacher(teacher);
							tgs.setGradeSubject(gradSub);
							tgsRepo.save(tgs);
							logger.error("Error occurred whil updating tgs " + tgs);
							logger.info("Admin (email: " + AuthController.getEmail() + ") updated tgs from:" + oldtgs
									+ " to: " + tgs);
							return new ResponseEntity<Teacher_Grade_Subject>(tgs, HttpStatus.OK);
						} else {
							return new ResponseEntity<String>("Compatance is not acceptable.",
									HttpStatus.NOT_ACCEPTABLE);
						}
					}
				} else {
					return new ResponseEntity<RestError>(new RestError(10, "There is no subject_grade with such ID"),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no tgs with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

}
