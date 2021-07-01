package com.couplecon.util;
import java.util.Properties;

public class Config {
	static Properties props = null;
	
	public static String getProperty(String key) {
		if (props == null) {
			props = new Properties();
			props.putAll(System.getenv());
		}
		return props.getProperty(key);
	}
}