package com.itprobuka.school_registar.entities.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MarkTypeDTO {
	
	@NotNull(message = "Mark type must be provided.")
	@Size(min = 2, max = 30, message = "Mark type must be between {min} and {max} characters long.")
	private String type;
	
	@NotNull(message = "Mark type desscription  must be provided.")
	@Size(min = 2, max = 300, message = "Mark type description must be between {min} and {max} characters long.")
	private String description;

	public MarkTypeDTO() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
