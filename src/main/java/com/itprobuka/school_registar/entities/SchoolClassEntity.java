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

@Entity
@Table(name = "school_class")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SchoolClassEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@Column
	private Integer label;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "grade")
	protected GradeEntity grade;

	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<UPupilEntity> pupils = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<Class_Teacher_Grade_Subject> ctgs = new ArrayList<>();

	public SchoolClassEntity() {
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

	public GradeEntity getGrade() {
		return grade;
	}

	public void setGrade(GradeEntity grade) {
		this.grade = grade;
	}

	public List<UPupilEntity> getPupils() {
		return pupils;
	}

	public void setPupils(List<UPupilEntity> pupils) {
		this.pupils = pupils;
	}

	public List<Class_Teacher_Grade_Subject> getCtgs() {
		return ctgs;
	}

	public void setCtgs(List<Class_Teacher_Grade_Subject> ctgs) {
		this.ctgs = ctgs;
	}

	@Override
	public String toString() {
		return "{ id:" + this.getId() + ", label:" + this.getLabel() + ", grade_id:" + this.getGrade().getId();
	}

}
