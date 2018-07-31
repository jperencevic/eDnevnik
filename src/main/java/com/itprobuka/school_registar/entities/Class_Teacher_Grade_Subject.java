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
@Table(name = "schedule")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Class_Teacher_Grade_Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@Column(name = "active")
	private Boolean active;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "competence")
	protected Teacher_Grade_Subject tgs;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "school_class")
	protected SchoolClassEntity schoolClass;

	@JsonIgnore
	@OneToMany(mappedBy = "ctgs", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<MarkEntity> marks = new ArrayList<>();

	public Class_Teacher_Grade_Subject() {
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

	public Teacher_Grade_Subject getTgs() {
		return tgs;
	}

	public void setTgs(Teacher_Grade_Subject tgs) {
		this.tgs = tgs;
	}

	public SchoolClassEntity getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(SchoolClassEntity schoolClass) {
		this.schoolClass = schoolClass;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

}
