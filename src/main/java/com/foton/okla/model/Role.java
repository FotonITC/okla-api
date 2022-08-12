package com.foton.okla.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Role {

	Role() {
	}

	Role(long id, String label, String description) {
		this.id = id;
		this.label = label;
		this.Description = description;
	}

	public static Role USER = new Role(1, "USER", "Simple user");

	@Id
	@GeneratedValue
	private long id;

	@NotNull
	private String label;

	private String Description;

}
