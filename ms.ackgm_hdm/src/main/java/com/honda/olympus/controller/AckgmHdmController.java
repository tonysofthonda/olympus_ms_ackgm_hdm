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

@RestController
public class AckgmHdmController {
	
	@Value("${service.success.message}")
	private String responseMessage;
	
	@Autowired
	private AckgmHdmService ackgmHdmService;
	
	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<ResponseVO> monitorFiles() {
		System.out.println(responseMessage);
		
		ackgmHdmService.callAckgmCheckHd();
		
		return new ResponseEntity<>(new ResponseVO(responseMessage, null), HttpStatus.OK);
	}

}
