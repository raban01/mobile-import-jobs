package com.safeway.app.emmd_batch.service.email;

import com.safeway.app.emmd_batch.util.EmmdEnum.EmmdAlertCode;

public interface EmailService {
	public void sendAlertEmail(EmmdAlertCode code, String jobName, String mailText, String runDate);
	public void sendMail(String subject, String jobName, String emailBody, String runDate);
	public void sendMail(String subject, String emailBody);
}

