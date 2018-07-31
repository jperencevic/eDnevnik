package com.itprobuka.school_registar.entities.dto;

import javax.validation.constraints.NotNull;

public class CtgsDTO {

	@NotNull
	private Boolean active;
	
	@NotNull
	private Integer tgsId;
	
	@NotNull
	private Integer schoolClassId;

	public CtgsDTO() {
		super();
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getTgsId() {
		return tgsId;
	}

	public void setTgsId(Integer tgsId) {
		this.tgsId = tgsId;
	}

	public Integer getSchoolClassId() {
		return schoolClassId;
	}

	public void setSchoolClassId(Integer schoolClassId) {
		this.schoolClassId = schoolClassId;
	}

}
