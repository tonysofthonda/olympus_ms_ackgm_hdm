package com.honda.olympus.scheduller;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honda.olympus.exception.MonitorException;
import com.honda.olympus.service.MonitorService;

@Component
public class MonitorScheduller {

	@Autowired
	MonitorService monitorService;
	
	@Value("${monitor.timelapse}")
	Long timeLapse;

	@Scheduled(fixedDelayString = "${monitor.timelapse}")
	public void monitorScheduledTask() throws IOException {
		System.out.println("Monitor Scheduller running - " + System.currentTimeMillis() / 1000);

		try {
			System.out.println("Monitor files:: start");
			Long startTime = System.nanoTime();
			monitorService.checkFiles();
			Long endTime = System.nanoTime();
			Long timeElapsed = endTime - startTime;
			
			System.out.println();
			System.out.println("Monitor files:: end / Execution time in milliseconds: " + timeElapsed / 1000000);
			
			Date expireDate = new Date(System.currentTimeMillis() + timeLapse);
			
			System.out.println("Next execution at: "+expireDate.toString());
		} catch (MonitorException e) {

			e.printStackTrace();
		}
	}

}
