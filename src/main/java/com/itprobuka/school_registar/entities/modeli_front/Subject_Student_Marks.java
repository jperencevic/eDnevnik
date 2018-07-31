package com.itprobuka.school_registar.entities.modeli_front;

import java.util.ArrayList;
import java.util.List;

import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;

public class Subject_Student_Marks {

	private UPupilEntity pupil;

	private List<MarkEntity> marks = new ArrayList<>();

	public Subject_Student_Marks() {
		super();
	}

	public UPupilEntity getPupil() {
		return pupil;
	}

	public void setPupil(UPupilEntity pupil) {
		this.pupil = pupil;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

}
