package com.honda.olympus.scheduller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honda.olympus.service.AckgmHdmService;

@Component
public class AckgmScheduller {
	
	
	@Autowired
	AckgmHdmService ackgmHdmService;

	@Scheduled(fixedDelay = 10000)
	public void monitorScheduledTask() throws IOException {
		System.out.println("Ackgm_hdm Scheduller running - " + System.currentTimeMillis() / 1000);

		try {
			ackgmHdmService.callAckgmCheckHd();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
