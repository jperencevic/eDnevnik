package com.itprobuka.school_registar.entities.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateGradeSubjectDTO {

	@NotNull(message = "Grades id must be provided.")
	@Digits(fraction = 0, integer = 10)
	private Integer graId;

	@NotNull(message = "Active must be provided.")
	private Boolean active;

	@NotNull(message = "Hours per week must be provided.")
	@Digits(fraction = 0, integer = 10)
	private Integer hoursPerWeek;

	@Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.")
	private String book;

	@Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.")
	private String workBook;

	@Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.")
	private String otherLiterature;

	public CreateGradeSubjectDTO() {
		super();
	}

	public Integer getGraId() {
		return graId;
	}

	public void setGraId(Integer graId) {
		this.graId = graId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getHoursPerWeek() {
		return hoursPerWeek;
	}

	public void setHoursPerWeek(Integer hoursPerWeek) {
		this.hoursPerWeek = hoursPerWeek;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public String getWorkBook() {
		return workBook;
	}

	public void setWorkBook(String workBook) {
		this.workBook = workBook;
	}

	public String getOtherLiterature() {
		return otherLiterature;
	}

	public void setOtherLiterature(String otherLiterature) {
		this.otherLiterature = otherLiterature;
	}
	
	

}
