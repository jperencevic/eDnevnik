package com.itprobuka.school_registar.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.MarkTypeEntity;

public interface MarkTypeRepository extends CrudRepository<MarkTypeEntity, Integer> {

	Optional<MarkTypeEntity> findByType (String type);
}
