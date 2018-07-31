package com.itprobuka.school_registar.entities.dto;

import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MarkDTO {

	@NotNull(message = "Mark must be provided.")
	@Digits(fraction = 0, integer = 10)
	@Min(value = 1)
	@Max(value = 5)
	private Integer mark;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
	@PastOrPresent
	@NotNull(message = "Date must be provided.")
	private LocalDate markGiven;

	@NotNull(message = "Pupils id must be provided.")
	@Digits(fraction = 0, integer = 10)
	private Integer pId;

	@NotNull(message = "Class_teacher_grade_subjects id must be provided.")
	@Digits(fraction = 0, integer = 10)
	private Integer ctgsId;

	@NotNull(message = "Mark types id must be provided.")
	@Digits(fraction = 0, integer = 10)
	private Integer mtId;

	public MarkDTO() {
		super();
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public LocalDate getMarkGiven() {
		return markGiven;
	}

	public void setMarkGiven(LocalDate markGiven) {
		this.markGiven = markGiven;
	}

	public Integer getpId() {
		return pId;
	}

	public void setpId(Integer pId) {
		this.pId = pId;
	}

	public Integer getCtgsId() {
		return ctgsId;
	}

	public void setCtgsId(Integer ctgsId) {
		this.ctgsId = ctgsId;
	}

	public Integer getMtId() {
		return mtId;
	}

	public void setMtId(Integer mtId) {
		this.mtId = mtId;
	}

}
