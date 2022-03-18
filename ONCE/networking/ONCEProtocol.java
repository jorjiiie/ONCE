package ONCE.networking;

public class ONCEProtocol {
	private static final int WAITING = 1;
	private static final int SEND_BLOCK = 5;
	private static final int SEND_TX = 15;

	private int state;
	private boolean host;
	// if you put a shareddata bc host yknow maybe??

	public ONCEProtocol(boolean host) {
		state = WAITING;
		host = host;
	}
}