package com.itprobuka.school_registar.repositories;

import org.springframework.data.repository.CrudRepository;

import com.itprobuka.school_registar.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {

	UserEntity findByEmail(String email);

}
