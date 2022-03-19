package ONCE.networking;

import java.io.Serializable;

public class MessageHeader implements Serializable {
	public final int TYPE;
	// don't know if this is necessary, since it may be able to just pick up the blocks on its own
	public final int SIZE;
	public final String CHECKSUM;

	public MessageHeader(int type, int sz, String checksum) {
		TYPE = type;
		SIZE = sz;
		CHECKSUM = checksum;
	}

}