package com.itprobuka.school_registar.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "grade_subject")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Grade_Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@Column
	private Boolean active;

	@JsonManagedReference
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "subject")
	protected SubjectEntity subject;

	@Column(name = "hours_per_week")
	private Integer hoursPerWeek;

	private String book;

	@Column(name = "woork_book")
	private String workBook;

	@Column(name = "other_literature")
	private String otherLiterature;

	@JsonManagedReference
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "grade")
	protected GradeEntity grade;

	@JsonIgnore
	@OneToMany(mappedBy = "gradeSubject", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<Teacher_Grade_Subject> tgs = new ArrayList<>();

	public Grade_Subject() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
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

	public GradeEntity getGrade() {
		return grade;
	}

	public void setGrade(GradeEntity grade) {
		this.grade = grade;
	}

	public List<Teacher_Grade_Subject> getTgs() {
		return tgs;
	}

	public void setTgs(List<Teacher_Grade_Subject> tgs) {
		this.tgs = tgs;
	}

	@Override
	public String toString() {
		return " id: " + this.getId() + ", book:" + this.getBook() + ", workBook:" + this.getWorkBook()
				+ ", otherLiterature:" + this.getOtherLiterature() + ", hoursPerWeek:" + this.getHoursPerWeek()
				+ ", grade_id:" + this.getGrade().getId() + ", active:" + this.getActive() + ", subject_id:"
				+ this.getSubject().getId() + "}";
	}

}
