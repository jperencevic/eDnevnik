package com.itprobuka.school_registar.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class UAdminEntity extends UserEntity {

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	// @Column(name = "start_date")
	// private LocalDate startDate;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	// @Column(name = "end_date")
	// private LocalDate endDate;

	public UAdminEntity() {
		super();
		// startDate = LocalDate.now();
	}

	// public LocalDate getStartDate() {
	// return startDate;
	// }
	//
	// public void setStartDate(LocalDate startDate) {
	// this.startDate = startDate;
	// }

	// public LocalDate getEndDate() {
	// return endDate;
	// }
	//
	// public void setEndDate(LocalDate endDate) {
	// this.endDate = endDate;
	// }

}
