package ONCE.networking.messages;

import java.io.Serializable;

// I should just be able to have a package-public file with all the messages

public class NetworkMessage implements Serializable, Payload {
	public InetAddress addr;
	public int port;

	public NetworkMessage(InetAddress a, int n) {
		addr = a;
		port = n;
	}
}