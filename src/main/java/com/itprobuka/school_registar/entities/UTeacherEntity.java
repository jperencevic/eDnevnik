package com.itprobuka.school_registar.entities;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itprobuka.school_registar.enumerations.ETitle;

@Entity
@Table(name = "teachers")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UTeacherEntity extends UserEntity {

	@Column(name = "no_of_licence")
	private String noOfLicence;

	@Enumerated(EnumType.STRING)
	@Column
	private ETitle title;

	@JsonIgnore
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<Teacher_Grade_Subject> tgs = new ArrayList<>();

	public UTeacherEntity() {
		super();
	}

	public String getNoOfLicence() {
		return noOfLicence;
	}

	public void setNoOfLicence(String noOfLicence) {
		this.noOfLicence = noOfLicence;
	}

	public ETitle getTitle() {
		return title;
	}

	public void setTitle(ETitle title) {
		this.title = title;
	}

	public List<Teacher_Grade_Subject> getTgs() {
		return tgs;
	}

	public void setTgs(List<Teacher_Grade_Subject> tgs) {
		this.tgs = tgs;
	}

	@Override
	public String toString() {
		return super.toString() + ", noOfLicence:" + this.getNoOfLicence() + ", title:" + this.getTitle() + "}";
	}

}
