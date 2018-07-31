package com.itprobuka.school_registar.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.UParentEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;
import com.itprobuka.school_registar.entities.modeli_front.Subject_Marks;
import com.itprobuka.school_registar.enumerations.EUserRole;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.MarkRepository;
import com.itprobuka.school_registar.repositories.SchoolClassRepository;
import com.itprobuka.school_registar.repositories.UParentRepository;
import com.itprobuka.school_registar.repositories.UPupilRepository;

@Service
public class PupilDaoImpl implements PupilDao {

	@Autowired
	UPupilRepository pupilRepo;

	@Autowired
	UParentRepository parentRepo;

	@Autowired
	SchoolClassRepository schoolClassRepo;

	@Autowired
	Class_Teacher_Grade_SubjectRepository ctgsRepo;

	@Autowired
	MarkRepository markRepo;

	@Override
	public ResponseEntity<?> postPutPupil(UPupilEntity pupil, UserDTO newPupil) {
		pupil.setName(newPupil.getName());
		pupil.setLastName(newPupil.getLastName());
		pupil.setEmail(newPupil.getEmail());
		pupilRepo.save(pupil);
		return new ResponseEntity<UPupilEntity>(pupil, HttpStatus.OK);
	}

	@Override
	public void addParent(UPupilEntity pupil, UserDTO parent) {
		if (parentRepo.findByEmail(parent.getEmail()).isPresent()) {
			UParentEntity parentFromDB = parentRepo.findByEmail(parent.getEmail()).get();
			if (parentFromDB.getRole() == EUserRole.NOT_ACTIVE) {
				parentFromDB.setRole(EUserRole.ROLE_PARENT);
				parentRepo.save(parentFromDB);
			}
			pupil.setParent(parentFromDB);
		} else {
			UParentEntity newParent = new UParentEntity();
			newParent.setName(parent.getName());
			newParent.setLastName(parent.getLastName());
			newParent.setEmail(parent.getEmail());
			newParent.setRole(EUserRole.ROLE_PARENT);
			parentRepo.save(newParent);

			pupil.setParent(newParent);
		}
		pupilRepo.save(pupil);

	}

	@Override
	public void removeParent(UPupilEntity pupil) {
		if (pupil.getParent() != null) {
			UParentEntity parent = pupil.getParent();
			if (parent.getChildren().size() == 1) {
				parent.setRole(EUserRole.NOT_ACTIVE);
				parentRepo.save(parent);
			}
		}
	}

	@Override
	public ResponseEntity<?> addSchoolClass(UPupilEntity pupil, Integer scId) {
		if (schoolClassRepo.existsById(scId)) {
			pupil.setSchoolClass(schoolClassRepo.findById(scId).get());
			pupilRepo.save(pupil);
			return new ResponseEntity<UPupilEntity>(pupil, HttpStatus.OK);
		} else
			return new ResponseEntity<RestError>(new RestError(10, "There is no schoolClass with such ID"),
					HttpStatus.NOT_FOUND);

	}

	@Override
	public ResponseEntity<?> getMarks(Integer pId, Integer sId) {

		if (pupilRepo.existsById(pId)) {
			if (!ctgsRepo.findBySchoolClassAndTgs_GradeSubject_Subject_id(
					pupilRepo.findById(pId).get().getSchoolClass(), sId).isEmpty()) {
				List<MarkEntity> marks = markRepo.findByPupil_idAndCtgs_Tgs_GradeSubject_Subject_id(pId, sId);
				return new ResponseEntity<List<MarkEntity>>(marks, HttpStatus.OK);

			} else {
				return new ResponseEntity<RestError>(
						new RestError(10, "Pupil with id does not have subject with given id"), HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<?> getMarksSubjects(Integer pId) {
		if (pupilRepo.existsById(pId)) {
			UPupilEntity pupil = pupilRepo.findById(pId).get();
			List<Subject_Marks> konacno = new ArrayList<>();
			List<Class_Teacher_Grade_Subject> ctgs = ctgsRepo.findBySchoolClass(pupil.getSchoolClass());
			for (Class_Teacher_Grade_Subject cc : ctgs) {
				Subject_Marks temp = new Subject_Marks();
				temp.setSubject(cc.getTgs().getGradeSubject().getSubject().getName());
				List<MarkEntity> markies = new ArrayList<>();
				List<MarkEntity> marks = markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(pupil,
						cc.getTgs().getGradeSubject().getSubject());
				for (MarkEntity m : marks) {
					markies.add(m);
				}
				temp.setMarks(markies);
				konacno.add(	 temp);
			}
			return new ResponseEntity<List<Subject_Marks>>(konacno, HttpStatus.OK);
		} else
			return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
					HttpStatus.NOT_FOUND);

	}

	@Override
	public Boolean isItAParrent(Integer pupil, Integer parent) {
		Boolean result = false;
		for (UPupilEntity p : parentRepo.findById(parent).get().getChildren()) {
			if (pupil.equals(p.getId())) {
				result = true;
			}
			break;
		}
		return result;
	}

	// @Override
	// public ResponseEntity<?> getMarksSubjects(Integer pId) {
	// if (pupilRepo.existsById(pId)) {
	// UPupilEntity pupil = pupilRepo.findById(pId).get();
	// List<Subject_Marks> subMar = new ArrayList<>();
	// Boolean active=true;
	// List<Class_Teacher_Grade_Subject>
	// klasici=ctgsRepo.findBySchoolClassAndActive(pupil.getSchoolClass(), active);
	// for (Class_Teacher_Grade_Subject ctgs : klasici) {
	//
	// Subject_Marks sm = new Subject_Marks();
	// List<MarkEntity> marks =
	// markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject_name(pupil,
	// ctgs.getTgs().getGradeSubject().getSubject().getName());
	//// List<Mark_front> markSM = new ArrayList<>();
	// List<String> ocenice =new ArrayList<>();
	// for (MarkEntity m : marks) {
	// ocenice.add(""+m.getMark());
	//// Mark_front marki=new Mark_front();
	//// marki.setId(""+m.getId());
	//// marki.setValue(m.getMark());
	//// markSM.add(marki);
	// }
	// sm.setSubject(ctgs.getTgs().getGradeSubject().getSubject().getName());
	// sm.setMarks(ocenice);
	// subMar.add(sm);
	// }
	// return new ResponseEntity<List<Subject_Marks>>(subMar, HttpStatus.OK);
	//
	// } else {
	// return new ResponseEntity<RestError>(new RestError(10, "There is no pupil
	// with such ID"),
	// HttpStatus.NOT_FOUND);
	// }
	// }

}
