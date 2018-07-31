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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "grade")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GradeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@Column(name = "label")
	private Integer label;

	@JsonBackReference
	@OneToMany(mappedBy = "grade", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<Grade_Subject> gradeSubject = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "grade", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<SchoolClassEntity> schoolClass = new ArrayList<>();

	public GradeEntity() {
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

	public Integer getLabel() {
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}

	public List<Grade_Subject> getGradeSubject() {
		return gradeSubject;
	}

	public void setGradeSubject(List<Grade_Subject> gradeSubject) {
		this.gradeSubject = gradeSubject;
	}

	public List<SchoolClassEntity> getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(List<SchoolClassEntity> schoolClass) {
		this.schoolClass = schoolClass;
	}

}
