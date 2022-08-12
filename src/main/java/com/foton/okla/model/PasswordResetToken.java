package com.foton.okla.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class PasswordResetToken {

	public PasswordResetToken() {
	}

	public PasswordResetToken(String token, OklaUser user) {
		this.token = token;
		this.user = user;
	}

	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String token;

	@OneToOne
	private OklaUser user;

	private Date expiryDate;
}