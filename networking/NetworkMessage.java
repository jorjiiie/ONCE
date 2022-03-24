import java.io.*;
import java.net.*;
public class NetworkMessage extends MessageHeader implements Serializable {
	public InetAddress addr;
	public int port;
	public NetworkMessage(InetAddress a, int n) {
		super(0);
		addr = a;
		port = n;
	}
}