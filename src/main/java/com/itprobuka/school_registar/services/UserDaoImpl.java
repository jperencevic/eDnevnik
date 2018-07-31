package com.itprobuka.school_registar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.UserEntity;
import com.itprobuka.school_registar.repositories.UserRepository;
import com.itprobuka.school_registar.util.Encryption;

@Service
public class UserDaoImpl implements UserDao {

	@Autowired
	UserRepository userRepo;

	@Override
	public Integer getLoggedInId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return userRepo.findByEmail(email).getId();
	}

	@Override
	public UserEntity getLoggedInUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return userRepo.findByEmail(email);
	}

	@Override
	public ResponseEntity<?> updatePasword(Integer id, String oldP, String newP) {
		if (userRepo.existsById(id)) {
			UserEntity user = userRepo.findById(id).get();

			if (Encryption.checkPassword(oldP, user.getPassword())) {
				user.setPassword(Encryption.getPassEncoded(newP));
				if (user.getFirstLog()) {
					user.setFirstLog(false);
				}
				userRepo.save(user);
				return new ResponseEntity<UserEntity>(user, HttpStatus.OK);

			} else {
				return new ResponseEntity<RestError>(new RestError(11, "Wrong password!"), HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no user with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

}
