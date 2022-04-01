package ONCE.networking;

import ONCE.networking.messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
/**
 * Abstract class to define a communication protocol
 * I do it this way because its future proofing apparently
 * @author jorjiiie
 */

public abstract class Protocol extends Thread {
	private InputStream in;
	private OutputStream out;

	/**
	 * Empty constructor
	 */
	public Protocol() {
		in = null;
		out = null;
	}
	/**
	 * Constructor
	 * @param _in input stream
	 * @param _out output stream
	 */
	public Protocol(InputStream _in, OutputStream _out) {
		in = _in;
		out = _out;
	}

	/**
	 * Defines behvaior when a connection is achieved with another node
	 * @param manager NetManager to use if something must be done
	 * @param soc connected socket
	 */
	public abstract void onConnect(NetManager manager, Socket soc, ServerSocket server);

	/**
	 * Defines a method for reading a message
	 * @return message that was read
	 */
	public abstract Message readMessage();

	/**
	 * Defines a method for writing a message
	 * @param msg message to send
	 */
	public abstract void writeMessage(Message msg);

	/**
	 * Returns the InputStream
	 * @return the InputStream of this protocol
	 */
	public InputStream getInputStream() {
		return in;
	}

	/**
	 * Returns the OutputStream
	 * @return the OutputStream of this protocol
	 */
	public OutputStream getOutputStream() {
		return out;
	}

}