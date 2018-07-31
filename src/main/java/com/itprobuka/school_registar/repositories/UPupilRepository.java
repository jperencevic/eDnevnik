package com.itprobuka.school_registar.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.itprobuka.school_registar.entities.UPupilEntity;

@Repository
public interface UPupilRepository extends CrudRepository<UPupilEntity, Integer> {

}
