package com.safeway.app.emmd_batch.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.safeway.app.emmd_batch.util.EmmdEnum;

public class AlertServiceImpl implements AlertService{
	private static final Logger LOGGER = LoggerFactory.getLogger(AlertServiceImpl.class);
	
/*	public void createAlert(String code, String alertMessage) {
		LOG.info("ERROR CODE="+code+" MESSAGE="+code+ ": "+ alertMessage);
	}*/
	
	public void createLog(String alertMessage) {
		LOGGER.info(alertMessage);
	}

	public void createAlert(EmmdEnum.EmmdAlertCode code, String alertMessage) {
		LOGGER.info("ERROR CODE="+code.getCode()+" MESSAGE="+code.getMsg()+ ": "+ alertMessage);
	}

	public void createAlert(EmmdEnum.EmmcErrorCode code, String alertMessage) {
		LOGGER.info("ERROR CODE="+code.getCode()+" MESSAGE="+code.getMsg()+ ": "+ alertMessage);
	}

	
}
