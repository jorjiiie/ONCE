package ONCE.networking;

import ONCE.core.*;
import ONCE.networking.messages.*;

import java.io.*;
import java.net.*;

// could extend Socket
/**
 * Class that handles a single connection as a listener or a broadcaster to a node
 * @author jorjiiie
 */ 
public class NetSocket {

	NetPair parent;
	Socket socket;
	

	boolean ready = false;
	private String name = "DEFAULT SOCKET";

	Protocol proto;

	/**
	 * Normal constructor
	 * @param soc Socket to connect to
	 */
	public NetSocket(Socket soc) {
		socket = soc;
	}

	/**
	 * Secondary constructor
	 * @param addr address to connect to
	 * @param port port to connect to
	 */
	public NetSocket(InetAddress addr, int port) {
		try {
			socket = new Socket(addr, port);
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Couldn't connect to " + addr + " on port " + port, name);
		}
	}

	/**
	 * Tertiary constructor
	 * @param addr address to connect to
	 * @param port to connect to
	 */
	public NetSocket(String addr, int port) {
		try {
			socket = new Socket(addr, port);
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Couldn't connect to " + addr + " on port " + port, name);
		}
	}

	/**
	 * Initialize the communication protocol that will handle data transmission
	 */
	public void initProtocol() {
		// depending on version stuff, change this
		try {
			proto = new ObjectProtocol(this);
		} catch (Exception e) {
			proto.closed = true;
			e.printStackTrace();
			Logging.log("Couldn't initialize communication protocol");
		}
	}

	/**
	 * Disconnect this socket by shutting down inputs and outputs, and finally closes the socket
	 */
	// probably can cause some error if you shutdown immediately after sending something
	public void disconnect() {
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (SocketException e) {
			// good
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while closing connection to " + socket, name);
		}
	}

	/**
	 * Disconnects and sends a disconnect message to the connected socket
	 */
	public void disconnectWithMessage() {

		// this should be part of protocol
		MessageHeader m = new MessageHeader(MessageHeader.DISCONNECT_MESSAGE, System.currentTimeMillis(), null);
		Message msg = new Message(m, null);
		proto.sendMessage(msg);

		disconnect();
	}

	/**
	 * Sends the connection information of the host to the connected socket to allow the incoming connection
	 * @param addr address of the host
	 * @param port port of the host
	 */
	public void connect(InetAddress addr, int port) {
		// sends a connection message w the info of this stuff
		// do we really checksum the payload?

		proto.connect(addr, port);
	}

	/**
	 * Turn on and listen for node traffic
	 * Spawns a new thread and passes the input/output to a protocol
	 */
	public void startListening() {		
		Logging.log("Started to listen from " + socket,name);

		proto.run();
		
	}

	/**
	 * Set name of the socket (Listener, broadcaster)
	 * @param n name of the socket
	 */
	public void setName(String n) {
		name = n;
	}

	/** 
	 * Sets the parent of this socket to the pair
	 * @param np NetPair that holds a reference to this socket
	 */
	public void pairUp(NetPair np) {
		parent = np;
	}

}