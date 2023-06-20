package com.honda.olympus.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.service.AckgmHdmService;
import com.honda.olympus.vo.ResponseVO;

import io.swagger.v3.oas.annotations.Operation;
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
	
	@Autowired
	private HttpServletRequest request;

	@Operation(summary = "Force AckgmCheckHd processing once")
	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVO> monitorFiles() {
		log.info("Ackgm_hdm:: Calling FORCE AckgmCheckHd:: Start");

		String ipAddress = request.getRemoteAddr();
		
		ackgmHdmService.callAckgmCheckHd(ipAddress);
		
		log.info("Ackgm_hdm:: Calling FORCE AckgmCheckHd:: End");

		return new ResponseEntity<>(new ResponseVO(serviceName,1L,responseMessage, ""), HttpStatus.OK);
	}

}
