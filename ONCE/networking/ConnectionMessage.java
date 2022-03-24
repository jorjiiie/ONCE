package ONCE.networking;

// i love oop
import java.io.Serializable;

public class ConnectionMessage extends Message implements Serializable {
	public final int NODE_VERSION;
	public final int BLOCK_COUNT;
	public final String LAST_BLOCK;
	public ConnectionMessage(int version, int cnt, String block) {
		super(6);
		NODE_VERSION = version;
		BLOCK_COUNT = cnt;
		LAST_BLOCK = block;
	}
}