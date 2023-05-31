package com.honda.olympus.exception;

public class MonitorException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5188340524260570979L;

	public MonitorException(String message) {
		super(message);
	}

	public MonitorException(Throwable ex) {
		super(ex);
	}

	public MonitorException(String message, Throwable ex) {
		super(message, ex);
	}

}
