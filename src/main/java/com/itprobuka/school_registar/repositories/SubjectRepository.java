package com.itprobuka.school_registar.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.SubjectEntity;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {

	Optional<SubjectEntity> findByNameIgnoreCase(String name);
}
