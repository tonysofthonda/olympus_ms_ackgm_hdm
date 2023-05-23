package com.honda.olympus.vo;

import java.util.List;

public class MaxTransitCallVO {

	private String request;

	private List<String> details;

	public MaxTransitCallVO() {
		super();
	}

	public MaxTransitCallVO(String request, List<String> details) {
		super();
		this.request = request;
		this.details = details;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

}
