package com.foton.okla.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PasswordRest {

	@Length(min = 9)
	private String newPassword;
	
	@Length(min = 9)
	private String confirmPassword;
	
	@NotNull
	private String token;
}