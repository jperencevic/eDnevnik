package com.itprobuka.school_registar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.SchoolClassEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.UTeacherEntity;

public interface Class_Teacher_Grade_SubjectRepository extends CrudRepository<Class_Teacher_Grade_Subject, Integer> {

	Optional<Class_Teacher_Grade_Subject> findBySchoolClassAndTgs_GradeSubject_Subject(SchoolClassEntity schoolClass,
			SubjectEntity subject);

	List<Class_Teacher_Grade_Subject> findBySchoolClassAndTgs_GradeSubject_Subject_id(SchoolClassEntity schoolClass,
			Integer sId);;

	List<Class_Teacher_Grade_Subject> findByTgs_Teacher(UTeacherEntity teacher);

	List<Class_Teacher_Grade_Subject> findByTgs_Teacher_idAndActive(Integer tId, Boolean ac);

	List<Class_Teacher_Grade_Subject> findBySchoolClassAndActive(SchoolClassEntity sc, Boolean ac);

	List<Class_Teacher_Grade_Subject> findBySchoolClass(SchoolClassEntity sc);

	Optional<Class_Teacher_Grade_Subject> findBySchoolClass_IdAndTgs_idAndActive(Integer scId, Integer tgsId, Boolean ac);
}
