package com.itprobuka.school_registar.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "competence")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Teacher_Grade_Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "grade_subject")
	protected Grade_Subject gradeSubject;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	protected UTeacherEntity teacher;

	@JsonIgnore
	@OneToMany(mappedBy = "tgs", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<Class_Teacher_Grade_Subject> ctgs = new ArrayList<>();

	public Teacher_Grade_Subject() {
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

	public Grade_Subject getGradeSubject() {
		return gradeSubject;
	}

	public void setGradeSubject(Grade_Subject gradeSubject) {
		this.gradeSubject = gradeSubject;
	}

	public UTeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(UTeacherEntity teacher) {
		this.teacher = teacher;
	}

	public List<Class_Teacher_Grade_Subject> getCtgs() {
		return ctgs;
	}

	public void setCtgs(List<Class_Teacher_Grade_Subject> ctgs) {
		this.ctgs = ctgs;
	}

	@Override
	public String toString() {
		return "{ id:" + this.getId() + ", teacher_id: " + this.getTeacher().getId() + ", tgs_id: "
				+ this.getGradeSubject().getId() + " }";
	}

}
