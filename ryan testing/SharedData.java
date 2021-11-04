public class SharedData {
	public volatile boolean flagA, flagB, flagE;
	public volatile String message;
	public volatile String message2;
	public SharedData(String mess) {
		message = mess;
	}
	public String toString() {
		return "" + flagA + " " + flagB + " " + message + " " + message2;
	}
}