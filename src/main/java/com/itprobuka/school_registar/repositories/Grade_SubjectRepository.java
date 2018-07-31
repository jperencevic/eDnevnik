package com.itprobuka.school_registar.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.Grade_Subject;

public interface Grade_SubjectRepository extends CrudRepository<Grade_Subject, Integer> {

	Optional<Grade_Subject> findByGrade_idAndSubject_id(Integer gIs, Integer sId);

	Grade_Subject findByGrade_label(Integer label);

	List<Grade_Subject> findByGrade_LabelIsLessThan(Integer label);

	List<Grade_Subject> findBySubject_idAndGrade_LabelIsGreaterThan(Integer id, Integer label);
}
