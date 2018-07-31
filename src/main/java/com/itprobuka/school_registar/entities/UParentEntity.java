package com.itprobuka.school_registar.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "parents")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UParentEntity extends UserEntity {

	 @JsonBackReference
//	@JsonIgnore
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	protected List<UPupilEntity> children = new ArrayList<>();

	public UParentEntity() {
		super();
	}

	public List<UPupilEntity> getChildren() {
		return children;
	}

	public void setChildren(List<UPupilEntity> children) {
		this.children = children;
	}

}
