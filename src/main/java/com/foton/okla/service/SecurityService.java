package com.foton.okla.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foton.okla.model.PasswordResetToken;
import com.foton.okla.repository.PasswordTokenRepository;

@Service
public class SecurityService {
	
	@Autowired
	private PasswordTokenRepository passwordTokenRepo;
	
	public String validatePasswordResetToken(String token) {
		final PasswordResetToken passToken = passwordTokenRepo.findByToken(token);

		return !isTokenFound(passToken) ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
	}

	private boolean isTokenFound(PasswordResetToken passToken) {
		return passToken != null;
	}

	private boolean isTokenExpired(PasswordResetToken passToken) {
		final Calendar cal = Calendar.getInstance();
//		return passToken.getExpiryDate().before(cal.getTime());
		return false;
	}
}
