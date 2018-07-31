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
import com.itprobuka.school_registar.entities.GradeEntity;
import com.itprobuka.school_registar.entities.SchoolClassEntity;
import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.GradeRepository;
import com.itprobuka.school_registar.repositories.SchoolClassRepository;
import com.itprobuka.school_registar.repositories.Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.UPupilRepository;

@Service
public class SchoolClassDaoImpl implements SchoolClassDao {

	@Autowired
	SchoolClassRepository schoolClassRepo;

	@Autowired
	Teacher_Grade_SubjectRepository teacherGradeSubjectRepo;

	@Autowired
	Class_Teacher_Grade_SubjectRepository classTeacherGradeSubjectRepo;

	@Autowired
	UPupilRepository pupilRepo;

	@Autowired
	GradeRepository gradeRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// ====================CREATE CTGS object===============================
	@Override
	public ResponseEntity<?> addTeacherGradeSubject(Integer scId, Integer tgsId) {
		if (schoolClassRepo.existsById(scId)) {
			if (teacherGradeSubjectRepo.existsById(tgsId)) {
				SchoolClassEntity schoolClass = schoolClassRepo.findById(scId).get();
				Teacher_Grade_Subject tgs = teacherGradeSubjectRepo.findById(tgsId).get();

				if (schoolClass.getGrade().equals(tgs.getGradeSubject().getGrade())) {
					if (!classTeacherGradeSubjectRepo.findBySchoolClass_IdAndTgs_idAndActive(scId, tgsId, true)
							.isPresent()) {
						Class_Teacher_Grade_Subject ctgs = new Class_Teacher_Grade_Subject();
						ctgs.setSchoolClass(schoolClass);
						ctgs.setTgs(tgs);
						ctgs.setActive(true);
						classTeacherGradeSubjectRepo.save(ctgs);
						return new ResponseEntity<Class_Teacher_Grade_Subject>(ctgs, HttpStatus.OK);
					} else {
						return new ResponseEntity<RestError>(new RestError(5,
								"Already exists in database"),
								HttpStatus.BAD_REQUEST);
					}
				} else {
					return new ResponseEntity<RestError>(new RestError(5,
							"Provided competance (teacher_grade_subject) grade does not match grade of schoolclass"),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<RestError>(
						new RestError(10, "There is no competance (teacher_grade_subject) with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
					HttpStatus.NOT_FOUND);
		}

	}

	// ===================adds pupil========================================
	@Override
	public ResponseEntity<?> addPupil(Integer scId, Integer pId) {
		if (schoolClassRepo.existsById(scId)) {
			if (pupilRepo.existsById(pId)) {
				UPupilEntity pupil = pupilRepo.findById(pId).get();
				SchoolClassEntity schoolClass = schoolClassRepo.findById(scId).get();
				pupil.setSchoolClass(schoolClass);
				pupilRepo.save(pupil);
				logger.error("Error ocurred while adding pupil to schoolclass");
				logger.info(
						"Admin (email:" + AuthController.getEmail() + ") added pupil to schoolClass " + schoolClass);
				return new ResponseEntity<UPupilEntity>(pupil, HttpStatus.OK);
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
					HttpStatus.NOT_FOUND);
		}

	}

	// ===================adds grade========================================
	@Override
	public ResponseEntity<?> addGrade(Integer scId, Integer gId) {
		if (schoolClassRepo.existsById(scId)) {
			if (gradeRepo.existsById(gId)) {
				GradeEntity grade = gradeRepo.findById(gId).get();
				SchoolClassEntity schoolClass = schoolClassRepo.findById(scId).get();
				schoolClass.setGrade(grade);
				schoolClassRepo.save(schoolClass);
				logger.error("Error ocurred while adding grade to schoolclass");
				logger.info(
						"Admin (email:" + AuthController.getEmail() + ") added grade to schoolClass " + schoolClass);
				return new ResponseEntity<SchoolClassEntity>(schoolClass, HttpStatus.OK);
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no grade with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no schoolclass with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

}
