package ONCE.networking;

import ONCE.networking.messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
/**
 * Abstract class to define a communication protocol
 * I do it this way because its future proofing apparently
 * @author jorjiiie
 */

public abstract class Protocol extends Thread {
	protected NetSocket socket;
	protected boolean closed;

	/**
	 * Empty constructor
	 */
	public Protocol() {
	}
	/**
	 * Constructor
	 * @param _in input stream
	 * @param _out output stream
	 */
	public Protocol(NetSocket soc) {
		socket = soc;
	}

	/**
	 * Defines behvaior when a connection is achieved with another node
	 * @param manager NetManager to use if something must be done
	 * @param soc connected socket
	 */
	public abstract void onConnect(NetManager manager, Socket soc, ServerSocket server);

	/**
	 * Defines behavior when connecting to another node
	 * @param addr address to connect to
	 * @param port port to connect to
	 */
	public abstract void connect(InetAddress addr, int port);
	/**
	 * Defines a method for reading a message
	 * @return message that was read
	 */
	public abstract Message readMessage();

	/**
	 * Defines a method for writing a message
	 * @param msg message to send
	 */
	public abstract void sendMessage(Message msg);


}