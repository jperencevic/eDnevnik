package com.itprobuka.school_registar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.Teacher_Grade_Subject;

public interface Teacher_Grade_SubjectRepository extends CrudRepository<Teacher_Grade_Subject, Integer> {

	Optional<Teacher_Grade_Subject> findByTeacher_idAndGradeSubject_id(Integer tId, Integer gsId);

	List<Teacher_Grade_Subject> findByTeacher_id(Integer id);
}
