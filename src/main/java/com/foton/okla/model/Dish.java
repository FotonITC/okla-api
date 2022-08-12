package com.foton.okla.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
@Entity
public class Dish {

	@Id
	@GeneratedValue
	private Long id;

	private String dbUri;

	@Column(name = "fromdb")
	private boolean fromDb;
	
	private Long creatorId;
	
	@NotNull
	@NotBlank
	private String label;

	@NotNull
	@NotBlank
	@Column(columnDefinition = "text")
	private String description;

	@NotNull
	@ElementCollection
	private List<String> types;
	
	private String inventor;
	private String calories;

	@ElementCollection
	private List<String> subjects;

	@ElementCollection
	private List<String> images;

	@Column(columnDefinition = "text")
	private String preparationSheet;

	private String preparationVideo;

	@NotNull
	@ElementCollection
	private List<String> ingredients;

	@ElementCollection
	private List<String> countries;

	@ElementCollection
	private List<String> variants;

	@ElementCollection
	private List<Long> similarDishes;

	@ElementCollection
	private List<String> restaurants;

	@ElementCollection
	private List<String> chefs;

}