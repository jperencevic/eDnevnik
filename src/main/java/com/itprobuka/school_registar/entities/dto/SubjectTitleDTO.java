package com.itprobuka.school_registar.entities.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SubjectTitleDTO {

	@NotNull(message = "Subject name must be provided.")
	@Size(min = 2, max = 30, message = "Subject name must be between {min} and {max} characters long.")
	private String title;

	public SubjectTitleDTO() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
