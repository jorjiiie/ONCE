import java.util.concurrent.atomic.AtomicBoolean;

public class SharedData {
	// flagA = new block
		// both listened and mined (same)
	// flagB = new transactions
		// will also copmile into new block but its not a priority so we can just take in the new block afterwards

	// flagE = i forgor 
	public volatile AtomicBoolean flagA, flagB, flagE;
	public volatile String message;
	public volatile String message2;
	public SharedData(String mess) {
		message = mess;
	}
	public String toString() {
		return "" + flagA + " " + flagB + " " + message + " " + message2;
	}
}
