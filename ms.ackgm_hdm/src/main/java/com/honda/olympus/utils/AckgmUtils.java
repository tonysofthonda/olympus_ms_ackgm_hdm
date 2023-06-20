package com.honda.olympus.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class AckgmUtils {
	
	private AckgmUtils() {
	    throw new IllegalStateException("Utility class");
	  }

	public static String getTimeStamp() {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(timestamp);
	}

}
