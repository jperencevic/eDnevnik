package com.itprobuka.school_registar.services;

import org.springframework.http.ResponseEntity;

import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.UserDTO;

public interface PupilDao {

	public ResponseEntity<?> postPutPupil(UPupilEntity pupil, UserDTO newPupil);

	public void removeParent(UPupilEntity pupil);

	public void addParent(UPupilEntity pupil, UserDTO parent);

	public ResponseEntity<?> addSchoolClass(UPupilEntity pupil, Integer scId);

	public ResponseEntity<?> getMarks(Integer pId, Integer sId);

	public ResponseEntity<?> getMarksSubjects(Integer pId);

	public Boolean isItAParrent(Integer pupil, Integer parent);

}
