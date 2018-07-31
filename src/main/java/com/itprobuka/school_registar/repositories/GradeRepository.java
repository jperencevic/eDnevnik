package com.itprobuka.school_registar.repositories;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.GradeEntity;

public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {

}
