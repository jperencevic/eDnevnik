package com.itprobuka.school_registar.services;

import java.time.LocalDate;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itprobuka.school_registar.controllers.AuthController;
import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.MarkTypeEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.entities.dto.MarkDTO;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.MarkRepository;
import com.itprobuka.school_registar.repositories.MarkTypeRepository;
import com.itprobuka.school_registar.repositories.SubjectRepository;
import com.itprobuka.school_registar.repositories.UPupilRepository;

@Service
public class MarkDaoImpl implements MarkDao {

	@Autowired
	MarkRepository markRepo;

	@Autowired
	MarkTypeRepository markTypeRepo;

	@Autowired
	UPupilRepository pupilRepo;

	@Autowired
	Class_Teacher_Grade_SubjectRepository classTeacherGradeSubjectRepo;

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	SubjectRepository subjectRepo;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Override
	public ResponseEntity<?> createMark(MarkDTO newMark) {
		if (markTypeRepo.existsById(newMark.getMtId())) {
			if (pupilRepo.existsById(newMark.getpId())) {
				if (classTeacherGradeSubjectRepo.existsById(newMark.getCtgsId())) {
					MarkTypeEntity type = markTypeRepo.findById(newMark.getMtId()).get();
					UPupilEntity pupil = pupilRepo.findById(newMark.getpId()).get();
					Class_Teacher_Grade_Subject ctgs = classTeacherGradeSubjectRepo.findById(newMark.getCtgsId()).get();
					if (pupil.getSchoolClass().equals(ctgs.getSchoolClass())) {
						if (!markRepo.findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(pupil,
								ctgs.getTgs().getGradeSubject().getSubject(), "zakljucna").isPresent()) {
							MarkEntity mark = new MarkEntity();
							mark.setMark(newMark.getMark());
							mark.setMarkGiven(newMark.getMarkGiven());
							mark.setMarkNoted(LocalDate.now());
							mark.setType(type);
							mark.setPupil(pupil);
							mark.setCtgs(ctgs);
							markRepo.save(mark);
							try {
								sendTemplateMessage(mark);

							} catch (Exception e) {
								return new ResponseEntity<RestError>(
										new RestError(1, "Error ocured: " + e.getMessage()),
										HttpStatus.INTERNAL_SERVER_ERROR);
							}
							logger.error("Error occurred while creating new mark");
							logger.info("User (email: " + AuthController.getEmail() + ") added new mark " + mark);
							return new ResponseEntity<MarkEntity>(mark, HttpStatus.OK);
						} else {
							return new ResponseEntity<RestError>(
									new RestError(9,
											"There is final mark, it is not alowed to add new marks after final"),
									HttpStatus.BAD_REQUEST);

						}
					} else {
						return new ResponseEntity<RestError>(new RestError(7,
								"Pupil can not be given mark due there is no connestion between puppil and class_teacher_grade_subject "),
								HttpStatus.NOT_FOUND);
					}
				} else {
					return new ResponseEntity<RestError>(
							new RestError(10, "There is no class_teacher_grade_subject with such ID"),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no mark type with such ID"),
					HttpStatus.NOT_FOUND);

		}
	}

	@Override
	public ResponseEntity<?> updateMark(Integer id, MarkDTO updateMark) {
		if (markTypeRepo.existsById(updateMark.getMtId())) {
			if (pupilRepo.existsById(updateMark.getpId())) {
				if (classTeacherGradeSubjectRepo.existsById(updateMark.getCtgsId())) {
					MarkTypeEntity type = markTypeRepo.findById(updateMark.getMtId()).get();
					UPupilEntity pupil = pupilRepo.findById(updateMark.getpId()).get();
					Class_Teacher_Grade_Subject ctgs = classTeacherGradeSubjectRepo.findById(updateMark.getCtgsId())
							.get();
					if (pupil.getSchoolClass().equals(ctgs.getSchoolClass())) {
						MarkEntity mark = markRepo.findById(id).get();
						MarkEntity oldmark = markRepo.findById(id).get();
						mark.setMark(updateMark.getMark());
						mark.setMarkGiven(updateMark.getMarkGiven());
						mark.setMarkNoted(LocalDate.now());
						mark.setType(type);
						mark.setPupil(pupil);
						mark.setCtgs(ctgs);
						markRepo.save(mark);
						try {
							sendTemplateMessageForUpdatedMark(mark, oldmark);

						} catch (Exception e) {
							return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
									HttpStatus.INTERNAL_SERVER_ERROR);
						}
						logger.error("Error occurred while updateing mark");
						logger.info("User (email: " + AuthController.getEmail() + ") updated mark from " + oldmark
								+ " to: " + mark);
						return new ResponseEntity<MarkEntity>(mark, HttpStatus.OK);

					} else {
						return new ResponseEntity<RestError>(new RestError(7,
								"Pupil can not be given mark due there is no connestion between puppil and class_teacher_grade_subject "),
								HttpStatus.NOT_FOUND);
					}
				} else {
					return new ResponseEntity<RestError>(
							new RestError(10, "There is no class_teacher_grade_subject with such ID"),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no mark type with such ID"),
					HttpStatus.NOT_FOUND);

		}
	}

	protected void sendTemplateMessage(MarkEntity mark) throws Exception {
		MimeMessage mail = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setTo(mark.getPupil().getParent().getEmail());
		helper.setSubject("Nova ocena");
		String text = "<html>" + "<body>" + "<table style='border:2px solid black'" + "<th>" + "<td>pupil</td>"
				+ "<td>mark</td>" + "<td>subject</td>" + "<td>teacher</td>" + "<td>date</td>" + "</th>" + "<tr>"
				+ "<td>" + mark.getPupil().getName() + " " + mark.getPupil().getLastName() + "</td>" + "<td>"
				+ mark.getMark() + "</td>" + "<td>" + mark.getCtgs().getTgs().getGradeSubject().getSubject().getName()
				+ "</td>" + "<td>" + mark.getCtgs().getTgs().getTeacher().getName() + " "
				+ mark.getCtgs().getTgs().getTeacher().getLastName() + "</td>" + "<td>" + mark.getMarkGiven() + "</td>"
				+ "</tr>" + "<table>" + "</body>" + "</html>";
		helper.setText(text, true);
		emailSender.send(mail);

	}

	protected void sendTemplateMessageForUpdatedMark(MarkEntity mark, MarkEntity oldmark) throws Exception {
		MimeMessage mail = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setTo(mark.getPupil().getParent().getEmail());
		helper.setSubject("Nova ocena");
		String text = "<html>" + "<body>" + "<h3>Ocena je promenjena</h3><table style='border:2px solid black'" + "<th>"
				+ "<td>pupil</td>" + "<td>mark</td>" + "<td>subject</td>" + "<td>teacher</td>" + "<td>date</td>"
				+ "</th>" + "<tr>" + "<td>" + mark.getPupil().getName() + " " + mark.getPupil().getLastName() + "</td>"
				+ "<td>" + mark.getMark() + "</td>" + "<td>"
				+ mark.getCtgs().getTgs().getGradeSubject().getSubject().getName() + "</td>" + "<td>"
				+ mark.getCtgs().getTgs().getTeacher().getName() + " "
				+ mark.getCtgs().getTgs().getTeacher().getLastName() + "</td>" + "<td>" + mark.getMarkGiven() + "</td>"
				+ "</tr>" + "<tr>" + "<td>" + oldmark.getPupil().getName() + " " + oldmark.getPupil().getLastName()
				+ "</td>" + "<td>" + oldmark.getMark() + "</td>" + "<td>"
				+ oldmark.getCtgs().getTgs().getGradeSubject().getSubject().getName() + "</td>" + "<td>"
				+ oldmark.getCtgs().getTgs().getTeacher().getName() + " "
				+ oldmark.getCtgs().getTgs().getTeacher().getLastName() + "</td>" + "<td>" + oldmark.getMarkGiven()
				+ "</td>" + "</tr>" + "<table>" + "</body>" + "</html>";
		helper.setText(text, true);
		emailSender.send(mail);

	}

//	@Override
//	public ResponseEntity<?> calculateFinal(Integer pId, Integer sId) {
//		if (pupilRepo.existsById(pId)) {
//			if (subjectRepo.existsById(sId)) {
//				UPupilEntity pupil = pupilRepo.findById(pId).get();
//				SubjectEntity subject = subjectRepo.findById(sId).get();
//				if (!markRepo.findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(pupil, subject, "zakljucna")
//						.isPresent()) {
//					List<MarkEntity> marks = markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(pupil, subject);
//					Double sum = 0.0;
//					for (MarkEntity mark : marks) {
//						sum += mark.getMark();
//					}
//					Integer finalMark = (int) Math.round(sum / marks.size());
//					logger.error("Error occurred while calculating final mark");
//					return new ResponseEntity<Integer>(finalMark, HttpStatus.OK);
//				} else {
//					return new ResponseEntity<RestError>(new RestError(9, "There is already final mark!"),
//							HttpStatus.BAD_REQUEST);
//				}
//			} else {
//				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
//						HttpStatus.NOT_FOUND);
//			}
//		} else {
//			return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
//					HttpStatus.NOT_FOUND);
//		}
//
//	}
	
	

	@Override
	public ResponseEntity<?> makeFinal(Integer pId, Integer sId, Integer suggestion) {
		if (pupilRepo.existsById(pId)) {
			if (subjectRepo.existsById(sId)) {
				UPupilEntity pupil = pupilRepo.findById(pId).get();
				SubjectEntity subject = subjectRepo.findById(sId).get();
				if (!markRepo.findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(pupil, subject, "FINAL")
						.isPresent()) {
					List<MarkEntity> marks = markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(pupil, subject);
					Double sum = 0.0;
					for (MarkEntity mark : marks) {
						sum += mark.getMark();
					}
					Integer finalMark = (int) Math.round(sum / marks.size());
					if (suggestion >= finalMark) {
						String finalType = "FINAL";
						MarkTypeEntity type = markTypeRepo.findByType(finalType).get();
						Class_Teacher_Grade_Subject ctgs = classTeacherGradeSubjectRepo
								.findBySchoolClassAndTgs_GradeSubject_Subject(pupil.getSchoolClass(), subject).get();
						MarkEntity givenFinal = new MarkEntity();
						givenFinal.setMark(suggestion);
						givenFinal.setMarkGiven(LocalDate.now());
						givenFinal.setMarkNoted(LocalDate.now());
						givenFinal.setPupil(pupil);
						givenFinal.setCtgs(ctgs);
						givenFinal.setType(type);
						markRepo.save(givenFinal);
						try {
							sendTemplateMessage(givenFinal);

						} catch (Exception e) {
							return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
									HttpStatus.INTERNAL_SERVER_ERROR);
						}
						logger.error("Error occurred while making final mark");
						logger.info(
								"User (email: " + AuthController.getEmail() + ") added new final mark " + givenFinal);
						return new ResponseEntity<MarkEntity>(givenFinal, HttpStatus.OK);
					} else {
						return new ResponseEntity<RestError>(
								new RestError(99, "You can not make a final mark less than calculated final mark!"),
								HttpStatus.BAD_REQUEST);
					}
				} else {
					return new ResponseEntity<RestError>(new RestError(9, "There is already final mark!"),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<RestError>(new RestError(10, "There is no subject with such ID"),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<RestError>(new RestError(10, "There is no pupil with such ID"),
					HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<?> calculateFinal(Integer pId, Integer sId) {
		UPupilEntity pupil = pupilRepo.findById(pId).get();
		SubjectEntity subject = subjectRepo.findById(sId).get();
		if (!markRepo.findByPupilAndCtgs_Tgs_GradeSubject_SubjectAndType_Type(pupil, subject, "zakljucna")
				.isPresent()) {
			List<MarkEntity> marks = markRepo.findByPupilAndCtgs_Tgs_GradeSubject_Subject(pupil, subject);
			Double sum = 0.0;
			for (MarkEntity mark : marks) {
				sum += mark.getMark();
			}
			Integer finalMark = (int) Math.round(sum / marks.size());
			logger.error("Error occurred while calculating final mark");
			return new ResponseEntity<Integer>(finalMark, HttpStatus.OK);
		} else {
			return new ResponseEntity<RestError>(new RestError(9, "There is already final mark!"),
					HttpStatus.BAD_REQUEST);
		}
	}
}
