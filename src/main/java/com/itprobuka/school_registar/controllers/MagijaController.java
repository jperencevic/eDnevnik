package com.itprobuka.school_registar.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itprobuka.school_registar.controllers.util.RestError;
import com.itprobuka.school_registar.entities.Class_Teacher_Grade_Subject;
import com.itprobuka.school_registar.entities.MarkEntity;
import com.itprobuka.school_registar.entities.MarkTypeEntity;
import com.itprobuka.school_registar.entities.SubjectEntity;
import com.itprobuka.school_registar.entities.UPupilEntity;
import com.itprobuka.school_registar.repositories.Class_Teacher_Grade_SubjectRepository;
import com.itprobuka.school_registar.repositories.MarkRepository;
import com.itprobuka.school_registar.repositories.MarkTypeRepository;
import com.itprobuka.school_registar.repositories.SubjectRepository;
import com.itprobuka.school_registar.repositories.UPupilRepository;

@RestController
@RequestMapping(path = "sr/medjik")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class MagijaController {

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	UPupilRepository pupilRepo;

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	MarkRepository markRepo;

	@Autowired
	MarkTypeRepository markTypeRepo;

	@Autowired
	Class_Teacher_Grade_SubjectRepository classTeacherGradeSubjectRepo;

	@GetMapping
	private UPupilEntity getPupil() {
		Integer id = 17;
		return pupilRepo.findById(id).get();
	}

	public ResponseEntity<?> calculateFinal(Integer pId, Integer sId) {
		if (pupilRepo.existsById(pId)) {
			if (subjectRepo.existsById(sId)) {
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
					return new ResponseEntity<Integer>(finalMark, HttpStatus.OK);
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

	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.GET, value = "/calculate_final/{pIds}/subj/{sIds}")
	private ResponseEntity<?> calculateFinalMark(@PathVariable String pIds, @PathVariable String sIds) {

		try {
			Integer pId = Integer.valueOf(pIds);
			Integer sId = Integer.valueOf(sIds);
			return calculateFinal(pId, sId);

		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
	@RequestMapping(method = RequestMethod.POST, value = "make_final/{pIds}/subj/{sIds}")
	private ResponseEntity<?> makeFinalMark(@PathVariable String pIds, @PathVariable String sIds,
			@RequestParam String sugg) {

		try {
			Integer pId = Integer.valueOf(pIds);
			Integer sId = Integer.valueOf(sIds);
			Integer suggestion = Integer.parseInt(sugg);
//			return makeFinal(pId, sId, suggestion);
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
									.findBySchoolClassAndTgs_GradeSubject_Subject(pupil.getSchoolClass(), subject)
									.get();
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
								return new ResponseEntity<RestError>(
										new RestError(1, "Error ocured: " + e.getMessage()),
										HttpStatus.INTERNAL_SERVER_ERROR);
							}

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
		} catch (Exception e) {
			return new ResponseEntity<RestError>(new RestError(1, "Error ocured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
