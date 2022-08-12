package com.foton.okla.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.sun.istack.NotNull;

import lombok.Data;

@Data
@Entity
public class OklaUser {

	public enum Gender {
		MALE, FEMALE
	}

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@NotBlank
	private String firstName;

	@NotNull
	@NotBlank
	private String lastName;

	@NotNull
	@NotBlank
	@Email
	private String email;

	private String phone;

	private String image;

	@NotNull
	@NotBlank
	private String password;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.DATE)
	private Date birthDay;

	@ManyToMany(fetch = FetchType.EAGER)
	List<Role> roles;
	
	private boolean activated;
}
