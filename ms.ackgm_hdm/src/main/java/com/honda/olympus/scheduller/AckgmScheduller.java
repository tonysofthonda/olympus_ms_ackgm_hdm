package com.honda.olympus.scheduller;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honda.olympus.service.AckgmHdmService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AckgmScheduller {

	@Autowired
	AckgmHdmService ackgmHdmService;

	@Value("${service.timelapse}")
	Long timeLapse;

	@Scheduled(fixedDelayString = "${service.timelapse}")
	public void monitorScheduledTask() throws IOException {
		log.info("Ackgm_hdm Scheduller running - " + System.currentTimeMillis() / 1000);

		try {
			log.info("Ackgm_hdm:: start");
			Long startTime = System.nanoTime();
			
			ackgmHdmService.callAckgmCheckHd();

			Long endTime = System.nanoTime();
			Long timeElapsed = endTime - startTime;

			log.info("Ackgm_hdm:: end / Execution time in milliseconds: {}", timeElapsed / 1000000);

			Date expireDate = new Date(System.currentTimeMillis() + timeLapse);

			log.info("Next ackgm_hdm execution at: {}",expireDate.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
