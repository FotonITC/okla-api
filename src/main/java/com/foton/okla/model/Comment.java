package com.foton.okla.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Comment {
	@Id
	@GeneratedValue
	private Long id;
	
	private Long creatorId;
	
	@NotNull
	private Long dishId;
	
	@NotNull
	@NotBlank
	@Column(columnDefinition = "text")
	private String text;
}
