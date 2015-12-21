package com.safeway.app.emmd_batch.service.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.safeway.app.emmd_batch.util.EmmdEnum.EmmdAlertCode;

public class EmailServiceImpl implements EmailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	//@Autowired 
	//private JavaMailSender mailSender;
	private SimpleMailMessage simpleMailMessage;
	private SimpleMailMessage simpleAlertMessage;
	
	private MailSender mailSenderCustom;
	
	public void sendAlertEmail(EmmdAlertCode code, String jobName, String mailText,  String runDate){
		sendAlert(code.getCode()+"-"+code.getMsg(),jobName, mailText, runDate);
	}

	public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
		this.simpleMailMessage = simpleMailMessage;
	}

	public void setMailSenderCustom(MailSender mailSenderCustom) {
		this.mailSenderCustom = mailSenderCustom;
	}
	
	public void setSimpleAlertMessage(SimpleMailMessage simpleAlertMessage) {
		this.simpleAlertMessage = simpleAlertMessage;
	}
	
	public void sendMail(String subject, String jobName, String emailBody, String runDate) {
		
		SimpleMailMessage message = new SimpleMailMessage(simpleMailMessage);
		message.setSubject(subject + " ");
		message.setText(String.format(simpleMailMessage.getText(), jobName, emailBody, runDate));

		mailSenderCustom.send(message);
		
	} 
	
	public void sendMail(String subject, String emailBody) {
		
		SimpleMailMessage message = new SimpleMailMessage(simpleMailMessage);
		message.setSubject(subject + " ");
		message.setText(emailBody);

		mailSenderCustom.send(message);
		
	} 
	
	public void sendAlert(String subject, String jobName, String emailBody, String runDate) {
		SimpleMailMessage message = new SimpleMailMessage(simpleAlertMessage);
		message.setSubject(subject + " ");
		message.setText(String.format(simpleAlertMessage.getText(), jobName, emailBody, runDate));

		mailSenderCustom.send(message);
	} 
	
}
