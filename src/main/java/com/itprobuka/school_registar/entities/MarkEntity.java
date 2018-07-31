package com.itprobuka.school_registar.entities;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "marks")
public class MarkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Integer version;

	@Column
	private Integer mark;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column
	private LocalDate markGiven;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column
	private LocalDate markNoted;

	@JsonManagedReference
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "type")
	protected MarkTypeEntity type;

	@JsonManagedReference
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "given_by")
	protected Class_Teacher_Grade_Subject ctgs;

	@JsonManagedReference
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "given_to")
	protected UPupilEntity pupil;

	public MarkEntity() {
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

	public LocalDate getMarkNoted() {
		return markNoted;
	}

	public void setMarkNoted(LocalDate markNoted) {
		this.markNoted = markNoted;
	}

	public MarkTypeEntity getType() {
		return type;
	}

	public void setType(MarkTypeEntity type) {
		this.type = type;
	}

	public Class_Teacher_Grade_Subject getCtgs() {
		return ctgs;
	}

	public void setCtgs(Class_Teacher_Grade_Subject ctgs) {
		this.ctgs = ctgs;
	}

	public UPupilEntity getPupil() {
		return pupil;
	}

	public void setPupil(UPupilEntity pupil) {
		this.pupil = pupil;
	}

	@Override
	public String toString() {
		return "{ id: " + this.getId() + ", mark:" + this.getMark() + ", type_id:" + this.getType().getId() + ", given:"
				+ this.getMarkGiven() + ", noted:" + this.getMarkNoted() + ", ctgs_id:" + this.getCtgs().getId()
				+ ", pupil_id:" + this.getPupil().getId() + " }";
	}

}
