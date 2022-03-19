package ONCE.networking;

import java.io.Serializable;

// send on connect
public class NodeInfo implements Serializable {
	public final int NODE_VERSION;
	public final int BLOCK_COUNT;
	public final String LAST_BLOCK;
	public NodeInfo(int version, int cnt, String block) {
		NODE_VERSION = version;
		BLOCK_COUNT = cnt;
		LAST_BLOCK = block;
	}
}