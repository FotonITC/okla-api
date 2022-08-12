package com.foton.okla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foton.okla.model.OklaUser;
import com.foton.okla.model.PasswordResetToken;


@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	PasswordResetToken findByToken(String token);
	PasswordResetToken findByUser(OklaUser user);
}