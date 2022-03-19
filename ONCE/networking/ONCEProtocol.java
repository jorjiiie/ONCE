package ONCE.networking;

public class ONCEProtocol {
	private static final int WAITING = 1;
	private static final int SEND_BLOCK = 5;
	private static final int SEND_TX = 15;

	private final int VERSION;
	private int state;
	private boolean host;
	// if you put a shareddata bc host yknow maybe??

	public ONCEProtocol(boolean _host, int version) {
		state = WAITING;
		host = _host;
		VERSION = version;
	}
	public boolean process() {
		return true;
	}
}