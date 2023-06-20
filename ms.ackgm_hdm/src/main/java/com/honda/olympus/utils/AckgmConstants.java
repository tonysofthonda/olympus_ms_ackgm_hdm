package com.honda.olympus.utils;

public class AckgmConstants {

	private AckgmConstants() {
		throw new IllegalStateException("AckgmConstants class");
	}

	public static final Long ZERO_STATUS = 0L;
	public static final Long ONE_STATUS = 1L;
	public static final String ACK_ORDER_REQUEST = "HONDA_ORDER_REQUEST_ACK";
	public static final String CREATE_STATUS = "CREATE";
	public static final String CHANGE_STATUS = "FAILED";
	public static final String CANCEL_STATUS = "CANCELED";
	
}
