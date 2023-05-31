package com.honda.olympus.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.exception.MonitorException;
import com.honda.olympus.service.MonitorService;
import com.honda.olympus.vo.ResponseVO;

@RestController
public class MonitorController {
	@Value("${service.success.message}")
	private String responseMessage;

	@Value("${service.name}")
	private String serviceName;

	@Value("${service.version}")
	private String version;

	@Value("${service.profile}")
	private String profile;

	@Autowired
	private MonitorService monitorService;

	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVO> monitorFiles() throws MonitorException, IOException {
		System.out.println(responseMessage);

		monitorService.checkFiles();

		return new ResponseEntity<>(new ResponseVO(serviceName, 1L, responseMessage, ""), HttpStatus.OK);
	}

}
