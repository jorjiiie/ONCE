import java.io.*;
import java.net.*;


/**
 * 
 * Pair of NetSockets that represents a connection to another node
 * @author jorjiiie
 * 
 */ 
public class NetPair {

	InetAddress addr;
	int port;

	NetSocket listener, broadcaster;

	boolean disconnected;

	/**
	 * Sole constructor
	 * @param _listener listening NetSocket, can be undefined while waiting for another connection
	 * @param _broadcaster broadcasting NetSocket, always should be defined
	 */
	public NetPair(NetSocket _listener, NetSocket _broadcaster) {
		listener = _listener;
		broadcaster = _broadcaster;
		// broadcaster is always defined, so we can grab the address from that
		addr = _broadcaster.socket.getInetAddress();
		port = _broadcaster.socket.getPort();
	}

	/**
	 * Shuts down listener and broadcaster, but may wait to finish their current operation. 
	 * If the socket is already disconnected, do nothing
	 */
	public void shutdown() {
		// do not remove
		if (disconnected)
			return;
		listener.disconnect();
		broadcaster.disconnectWithMessage();
	}

	/**
	 * Shuts down listener and broadcaster regardless of their current state
	 */
	public void forceShutdown() {

	}

	/**
	 * Disconnects a NetPair from the connected node. Sends a disconnect message to the other node.
	 * Removes itself from the parent array, removing references and allowing garbage collection to reclaim it
	 * If the NetPair is already disconnected, do not disconnect.
	 */
	public void disconnect() {

		if (disconnected) {
			// should implement some locks 
			Connector.self.remove(this);
			return;
		}

		listener.disconnect();
		broadcaster.disconnectWithMessage();

		disconnected = true;

		Connector.self.removeConnection(this);

		Logging.log("Successfully disconnected from " + addr + ":" + port);
	}
	
	/**
	 * Same as disconnect() but does not send a message to the other node. 
	 * Used when a disconnect message is recieved and sending a message would be redundant
	 */
	public void disconnectNoMessage() {

		if (disconnected)
			return;

		listener.disconnect();
		broadcaster.disconnect();

		disconnected = true;

		Connector.self.removeConnection(this);

		Logging.log("Successfully disconnected from " + addr + ":" + port);
	}

	/**
	 * Used when listener is ready to listen, and opens the node for listening to the other node
	 */
	public void ready() {
		listener.ready = true;
		listener.startListening();
	}

	/**
	 * Debugging function
	 */
	public void checkState() {
		Logging.log("state of sockets: " + listener.socket.isClosed() + broadcaster.socket.isClosed());
	}

	/**
	 * lol idk
	 */
	public void listenForMessages() {

	}

	/**
	 * Get address and port information
	 * @return information
	 */
	public String getInfo() {
		return addr + ":" + port;
	}

	/**
	 * Get advanced information
	 * @return information
	 */
	public String getAdvancedInfo() {
		return listener.socket + " " + broadcaster.socket;
	}

	

}