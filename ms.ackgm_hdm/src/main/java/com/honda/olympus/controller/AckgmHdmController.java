package com.honda.olympus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.service.AckgmHdmService;
import com.honda.olympus.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AckgmHdmController {

	@Value("${service.success.message}")
	private String responseMessage;

	@Value("${service.success.message}")
	private String successMessage;

	@Value("${service.name}")
	private String serviceName;

	@Autowired
	private AckgmHdmService ackgmHdmService;

	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVO> monitorFiles() {
		log.info("Ackgm_hdm:: Calling FORCE AckgmCheckHd:: Start");

		ackgmHdmService.callAckgmCheckHd();
		
		log.info("Ackgm_hdm:: Calling FORCE AckgmCheckHd:: End");

		return new ResponseEntity<>(new ResponseVO(serviceName,1L,responseMessage, ""), HttpStatus.OK);
	}

}
