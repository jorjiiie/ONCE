package ONCE.networking;

import java.net.*;
import java.io.*;

/**
 * MessageHeader that's very lazy on my end, but has type, checksum, and timestamp
 * no size because I'm incredibly lazy
 * @author jorjiiie
 */
public class MessageHeader implements Serializable {

	public static final int REJECTION_MESSAGE = -1;
	public static final int NETWORK_MESSAGE = 1;
	public static final int COMMUNICATION_MESSAGE = 2;

	public final int type;
	public final long timestamp;
	public final String checksum;
	// 1 - block
	// 2 - apple
	// 0 - connect message
	// -1 - disconnect message
	public MessageHeader(int _type, long _timestamp, String _checksum) {
		type = _type;
		timestamp = _timestamp;
		checksum = _checksum;
	}
}