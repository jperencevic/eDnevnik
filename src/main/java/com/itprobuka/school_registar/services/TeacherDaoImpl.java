package com.itprobuka.school_registar.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.AuthController;
import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.Grade_Subject;
import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.UTeacherEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.entities.modeli_front.Subject_Student_Marks;
import com.itprobuka.school_registar.enumerations.ETitle;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.MarkRepository;
import com.itprobuka.school_registar.repositories.SubjectRepository;
import com.itprobuka.school_registar.repositories.Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.UTeacherRepository;

@Service
public class TeacherDaoImpl implements TeacherDao {

	@Autowired
	UTeacherRepository teacherRepo;

	@Autowired
	Grade_SubjectRepository gradeSubjectRepo;

	@Autowired
	Teacher_Grade_SubjectRepository teacherGradeSubjectRepo;

	@Autowired
	Class_Teacher_Grade_SubjectRepository ctgsRepo;

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	MarkRepository markRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	// =======CREATES TEACHER-GRADE-SUBJECT OBJECT =====================
	@Override
	public ResponseEntity<?> addGradeSubject(Integer tId, Integer gsId) {
		if (teacherRepo.existsById(tId)) {
			if (gradeSubjectRepo.existsById(gsId)) {
				UTeacherEntity teacher = teacherRepo.findById(tId).get();
				Grade_Subject gradSub = gradeSubjectRepo.findById(gsId).get();
				Integer label = gradSub.getGrade().getLabel();

				if (teacherGradeSubjectRepo.findByTeacher_idAndGradeSubject_id(tId, gsId).isPresent()) {
					return new ResponseEntity<String>("Already exists in database.", HttpStatus.NOT_ACCEPTABLE);
				} else {
					if ((teacher.getTitle() == ETitle.SUBJECT_TEACHER && label > 4)
							|| (teacher.getTitle() == ETitle.LOWER_GRADES_TEACHER && label < 5)) {
						Teacher_Grade_Subject tgs = new Teacher_Grade_Subject();
						tgs.setTeacher(teacher);
						tgs.setGradeSubject(gradSub);
						teacherGradeSubjectRepo.save(tgs);
						logger.error("Error occured while creating tgs");
						logger.info("Admin (email: " + AuthController.getEmail() + ")  added new tgs " + tgs);
						return new ResponseEntity<Teacher_Grade_Subject>(tgs, HttpStatus.OK);
					} else {
						return new ResponseEntity<String>("Compatance is not acceptable.", HttpStatus.NOT_ACCEPTABLE);
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

	}

	// ===============CREATES LOWER GRADE TEACHER================
	// =======objects of teacher-grade-subject===================
	@Override
	public ResponseEntity<?> lowerGradeTeacher(Integer tId) {
		if (teacherRepo.existsById(tId)) {
			for (Grade_Subject gs : gradeSubjectRepo.findByGrade_LabelIsLessThan(5)) {
				addGradeSubject(tId, gs.getId());
				logger.error("Error occured while creating tgs");
				logger.info("Admin (email: " + AuthController.getEmail()
						+ ") added new grade_subject to lower grade teacher #id " + tId);
			}
			return new ResponseEntity<List<Teacher_Grade_Subject>>(teacherGradeSubjectRepo.findByTeacher_id(tId),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

	// ===============CREATES SUBJECT TEACHER====================
	// =======objects of teacher-grade-subject===================
	@Override
	public ResponseEntity<?> subjectTeacher(Integer tId, Integer sId) {
		if (teacherRepo.existsById(tId)) {
			for (Grade_Subject gs : gradeSubjectRepo.findBySubject_idAndGrade_LabelIsGreaterThan(sId, 4)) {
				addGradeSubject(tId, gs.getId());
				logger.error("Error occured while creating tgs");
				logger.info("Admin (email: " + AuthController.getEmail()
						+ ")  added new grade_subject to subject teacher #id " + tId);
			}
			return new ResponseEntity<List<Teacher_Grade_Subject>>(teacherGradeSubjectRepo.findByTeacher_id(tId),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

	// ===========ADD ATRIBUTES TO TACHER=======================
	@Override
	public ResponseEntity<?> postPutTeacher(UTeacherEntity teacher, UserDTO newTeacher) {
		if (newTeacher.getTeacherTitle() == null) {
			return new ResponseEntity<RestError>(new RestError(2, "Title must be provided"), HttpStatus.BAD_REQUEST);
		}
		if (newTeacher.getTeacherNoOfLicence() == null) {
			return new ResponseEntity<RestError>(new RestError(2, "Teacher number of licence must be provided"),
					HttpStatus.BAD_REQUEST);
		}
		teacher.setName(newTeacher.getName());
		teacher.setLastName(newTeacher.getLastName());
		teacher.setEmail(newTeacher.getEmail());
		teacher.setTitle(newTeacher.getTeacherTitle());
		teacher.setNoOfLicence(newTeacher.getTeacherNoOfLicence());
		teacherRepo.save(teacher);
		logger.error("Error occured while creating tgs");
		logger.info("Admin (email: " + AuthController.getEmail() + ")  added new/updated teacher " + teacher);

		return new ResponseEntity<UTeacherEntity>(teacher, HttpStatus.OK);
	}

	// ====RESETS THE ACTIVE STATUS OF CONNECTED CTGS OBJECTS========
	@Override
	public void setNotActiveCtgs(UTeacherEntity teacher) {
		List<Class_Teacher_Grade_Subject> ctgsList = ctgsRepo.findByTgs_Teacher(teacher);
		for (Class_Teacher_Grade_Subject ctgs : ctgsList) {
			ctgs.setActive(false);
		}

	}

	// ==========GETS ALL OF CONNECTED CTGS OBJECTS===================
	@Override
	public ResponseEntity<?> getCTGS(Integer tId) {
		if (teacherRepo.existsById(tId)) {
			return new ResponseEntity<List<Class_Teacher_Grade_Subject>>(
					ctgsRepo.findByTgs_Teacher_idAndActive(tId, true), HttpStatus.OK);
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<?> getStudentsMarks(Integer tId, Integer ctgsId) {
		List<Subject_Student_Marks> ssm = new ArrayList<>();
		if (teacherRepo.existsById(tId)) {
			if (ctgsRepo.existsById(ctgsId)) {
				Class_Teacher_Grade_Subject ctgs = ctgsRepo.findById(ctgsId).get();
				UTeacherEntity teacher = teacherRepo.findById(tId).get();
				if (ctgs.getTgs().getTeacher().equals(teacher)) {
					List<UPupilEntity> pupils = ctgs.getSchoolClass().getPupils();
					SubjectEntity subject = ctgs.getTgs().getGradeSubject().getSubject();
					for (UPupilEntity p : pupils) {
						Subject_Student_Marks temp = new Subject_Student_Marks();
						List<MarkEntity> marks = markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(p, subject);
						temp.setPupil(p);
						temp.setMarks(marks);
						ssm.add(temp);
					}
					return new ResponseEntity<List<Subject_Student_Marks>>(ssm, HttpStatus.OK);
				} else {
					return new ResponseEntity<RestError>(
							new RestError(10, "Invalid input, teacher does not have that task"), HttpStatus.NOT_FOUND);
				}

			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no ctgs with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no teacher with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

}
