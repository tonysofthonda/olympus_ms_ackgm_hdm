package com.honda.olympus.vo;

import java.util.List;

import org.springframework.http.HttpStatus;

public class MaxTransitCallVO {

	private String rquest;

	private List<String> details;

	
	public MaxTransitCallVO() {
		super();
	}
	

	public String getRquest() {
		return rquest;
	}

	public void setRquest(String rquest) {
		this.rquest = rquest;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}


}
