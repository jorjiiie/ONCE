package ONCE.networking.messages;

import ONCE.core.HashUtils;

import java.io.Serializable;
import java.net.InetAddress;

// I should just be able to have a package-public file with all the messages

public class NetworkMessage implements Serializable, Payload {
	public final InetAddress addr;
	public final int port;
	public final int version;

	public NetworkMessage(InetAddress a, int n, int v) {
		addr = a;
		port = n;
		version = v;
	}
	public String checksum() {
		// replace with a proper checksum when you actually do it
		return HashUtils.sHash(""+addr+port+version);
	}
}