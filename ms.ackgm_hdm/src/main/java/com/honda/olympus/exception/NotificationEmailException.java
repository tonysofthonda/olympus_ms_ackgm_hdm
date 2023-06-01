package com.honda.olympus.exception;

public class NotificationEmailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3301101565847251974L;

	public NotificationEmailException(String message) {
		super(message);
	}

	public NotificationEmailException(Throwable ex) {
		super(ex);
	}

	public NotificationEmailException(String message, Throwable ex) {
		super(message, ex);
	}

}
