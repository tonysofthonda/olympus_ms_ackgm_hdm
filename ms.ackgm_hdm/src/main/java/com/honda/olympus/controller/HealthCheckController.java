package com.honda.olympus.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class HealthCheckController {

	@Value("${service.name}")
	private String name;
	
	@Value("${service.version}")
	private String version; 
	
	@Value("${service.profile}")
	private String profile;
	
	@Operation(summary = "Test service availability and version")
	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() 
	{
		String message = String.format("Honda Olympus [name: %s] [version: %s] [profile: %s] %s %s", 
			name, version, profile, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), TimeZone.getDefault().getID() );
		
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
}