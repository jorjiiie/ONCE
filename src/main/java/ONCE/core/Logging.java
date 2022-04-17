package ONCE.core;

import java.sql.Timestamp;
import java.math.BigInteger;
public class Logging {
	public static void log(String s) {
		// timestamp 
		System.out.println("[" + new Timestamp(System.currentTimeMillis()) + "]: " + s);
	}
	public static void log(String s, String adjective) {
		System.out.println("[" + new Timestamp(System.currentTimeMillis()) + " @ " + adjective + "]: " + s);
	}
	// lmao
	public static void logTx(Transaction tx) {
		log(tx.getHash() + ": " + HashUtils.sHash(tx.getSender().toString()) + " sending " + tx.getAmount() + " to " + HashUtils.sHash(tx.getReciever().toString()),"Transaction description");
	}

	public static void logBalance(BigInteger addr, Long amt) {
		log(HashUtils.sHash(addr.toString()) + " has " + amt + " coins");
	}
	public static void error(String s) {
		
	}
}