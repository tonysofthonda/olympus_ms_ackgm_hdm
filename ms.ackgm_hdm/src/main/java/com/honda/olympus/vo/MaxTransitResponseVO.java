package com.honda.olympus.vo;

import java.util.List;

import org.springframework.http.HttpStatus;

public class MaxTransitResponseVO {

	private String action;
	private String vehOrderNbr;
	private String reqstStatus;
	private List<String> mesagge;
	private String voLastChgTimestamp;

	private String rqstIdentifier;

	private HttpStatus status;

	public MaxTransitResponseVO() {
		super();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getVehOrderNbr() {
		return vehOrderNbr;
	}

	public void setVehOrderNbr(String vehOrderNbr) {
		this.vehOrderNbr = vehOrderNbr;
	}

	public String getReqstStatus() {
		return reqstStatus;
	}

	public void setReqstStatus(String reqstStatus) {
		this.reqstStatus = reqstStatus;
	}

	public List<String> getMesagge() {
		return mesagge;
	}

	public void setMesagge(List<String> mesagge) {
		this.mesagge = mesagge;
	}

	public String getVoLastChgTimestamp() {
		return voLastChgTimestamp;
	}

	public void setVoLastChgTimestamp(String voLastChgTimestamp) {
		this.voLastChgTimestamp = voLastChgTimestamp;
	}

	public String getRqstIdentifier() {
		return rqstIdentifier;
	}

	public void setRqstIdentifier(String rqstIdentifier) {
		this.rqstIdentifier = rqstIdentifier;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}
