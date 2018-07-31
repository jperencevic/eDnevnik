package com.itprobuka.school_registar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;

public interface MarkRepository extends CrudRepository<MarkEntity, Integer> {

	Optional<MarkEntity> findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(UPupilEntity pupil,
			SubjectEntity subject, String type);

	List<MarkEntity> findByPupilAndCtgs_Tgs_GradeSubject_Subject(UPupilEntity pupil, SubjectEntity subject);
	
	List<MarkEntity> findByPupil_idAndCtgs_Tgs_GradeSubject_Subject_id (Integer pId, Integer sId);
	
	List<MarkEntity> findByPupilAndCtgs_Tgs_GradeSubject_Subject_name (UPupilEntity pupil, String subject);

}
