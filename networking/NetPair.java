import java.io.*;
import java.net.*;


public class NetPair {

	InetAddress addr;
	int port;
	private NetSocket listener, broadcaster;

	boolean disconnected;

	public NetPair(NetSocket _listener, NetSocket _broadcaster) {
		listener = _listener;
		broadcaster = _broadcaster;
		// broadcaster is always defined, so we can grab the address from that
		addr = _broadcaster.socket.getInetAddress();
		port = _broadcaster.socket.getPort();
	}


	public void shutdown() {
		// do not remove
		if (disconnected)
			return;
		listener.disconnect();
		broadcaster.disconnectWithMessage();
	}
	public void disconnect() {

		if (disconnected) {
			// should implement some locks 
			Connector.self.remove(this);
			return;
		}

		listener.disconnect();
		broadcaster.disconnectWithMessage();

		disconnected = true;

		Connector.self.remove(this);

		Logging.log("Successfully disconnected from " + addr + ":" + port);
	}
	public void disconnectNoMessage() {

		if (disconnected)
			return;

		listener.disconnect();
		broadcaster.disconnect();

		disconnected = true;

		Connector.self.remove(this);

		Logging.log("Successfully disconnected from " + addr + ":" + port);
	}
	public void ready() {
		listener.ready = true;
	}
	public void checkState() {
		Logging.log("state of sockets: " + listener.socket.isClosed() + broadcaster.socket.isClosed());
	}
	public void listenForMessages() {
		/*
		Logging.log("Initializing listener " + listener);
		new Thread() {
			// will just crash when done or smth lol
			public void run() {
				Protocol proto = new Protocol();
				protocol.init(this);


			}

		}.start();
	*/
	}
	

}