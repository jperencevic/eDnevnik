package com.itprobuka.school_registar.entities.modeli_front;

import java.util.ArrayList;
import java.util.List;

import com.itprobuka.school_registar.entities.MarkEntity;

public class Subject_Marks {

	String subject;

	List<MarkEntity> marks = new ArrayList<>();

	public Subject_Marks() {
		super();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

}
