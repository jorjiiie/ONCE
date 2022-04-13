package ONCE.core;

import java.sql.Timestamp;

public class Logging {
	public static void log(String s) {
		// timestamp 
		System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "]: " + s);
	}
	public static void log(String s, String adjective) {
		System.out.println("[" + new Timestamp(System.currentTimeMillis()) + " @ " + adjective + "]: " + s);
	}
	public static void error(String s) {
		
	}
}