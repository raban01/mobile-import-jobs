package com.safeway.app.emmd_batch.service.email;

import com.safeway.app.emmd_batch.util.EmmdEnum;

public interface AlertService {
	//public void createAlert(String alertCode, String alertMessage);
	public void createLog(String alertMessage);
	public void createAlert(EmmdEnum.EmmcErrorCode code, String alertMessage);
	public void createAlert(EmmdEnum.EmmdAlertCode code, String alertMessage);

}
