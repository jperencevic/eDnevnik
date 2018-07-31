package com.itprobuka.school_registar.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.UParentEntity;

public interface UParentRepository extends CrudRepository<UParentEntity, Integer> {

	Optional<UParentEntity> findByEmail(String email);
}
