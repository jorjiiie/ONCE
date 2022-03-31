package ONCE.networking;

import ONCE.core.*;

import java.io.*;
import java.net.*;

// could extend Socket
/**
 * Class that handles a single connection as a listener or a broadcaster to a node
 * @author jorjiiie
 */ 
public class NetSocket {

	private NetPair parent;
	Socket socket;
	
	private ObjectOutputStream out;
	private ObjectInputStream in;

	boolean ready = false;
	private String name = "DEFAULT SOCKET";

	private Protocol proto;

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
	 * Initialize I/O for the socket into out and in
	 */
	public void initIO() {
		// i think initio should be a default?
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Couldn't open io streams of " + socket, name);
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

		try {
			MessageHeader m = new MessageHeader(-1);
			out.writeObject(m);
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (SocketException e) {
			// do nothing
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while closing connection to " + socket, name);
		}
	}

	/**
	 * Sends the connection information of the host to the connected socket to allow the incoming connection
	 * @param addr address of the host
	 * @param port port of the host
	 */
	public void connect(InetAddress addr, int port) {
		// sends a connection message w the info of this stuff
		// do we really checksum the payload?
		NetworkMessage info = new NetworkMessage(addr, port);

		MessageHeader header = new MessageHeader(MessageHeader.NETWORK_MESSAGE, System.currentTimeMillis(), null);

		// write the message to out
		sendMessage(msg);
	}

	/**
	 * Method to read a message, should only be used on connection.
	 * Other messages should be read by the protocol
	 * @return the MessageHeader that should represent a network connection
	 */
	public MessageHeader readMessage() {
		try {
			MessageHeader msg = (MessageHeader) in.readObject();
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while reading message from " + socket,name);
			return null;
		}
	}

	/**
	 * Turn on and listen for node traffic
	 * Spawns a new thread and passes the input/output to a protocol
	 */
	public void startListening() {
		// return;
		
		Logging.log("Started to listen from " + socket,name);
		// this should be interruptable or you can just catch the close exception as shut down connection
		// run a protocol in a new thread? that gets the netpair 

		// proto = new Protocol()
		// needs a new thread so we can interrupt
		new Thread() {
			public void run() {
				Logging.log(socket + " is " + (ready?"ready":"not ready"));
				while (ready) {
					try {
						MessageHeader m = (MessageHeader) in.readObject();
						if (m.type == -1) {
							// disconnect message
							// not really necessary
							Logging.log("Recieved disconnect message from " + socket + ", shutting down");
							parent.disconnectNoMessage();
							return;
						} else if (m.type == 1) {
							continue;
						} else if (m.type == 2) {
							BlockMessage bm = (BlockMessage) m;
							Logging.log("Recieved block " + bm.block + " from " + socket);
							// Client.self.addBlock(bm.block);
						} else if (m.type == 3) {
							// say something message
							CommunicationMessage cm = (CommunicationMessage) m;
							Logging.log("Recieved: " + cm.message + " from: " +cm.author);
						}
					} catch (IOException e) {
						// server shut down

						// don't need this bc this is only if shut down
						// e.printStackTrace();
						// lol this works out quite well, don't even need disconnect message
						Logging.log(socket + " was shut down, closing pair");
						parent.disconnectNoMessage();
						return;
					} catch (Exception e) {
						e.printStackTrace();
						Logging.log("Error while reading ");
					}
				}
			}
		}.start();
		
	}

	/**
	 * Sends a message to the connected socket
	 * Should not be used probably
	 * @param msg MessageHeader to be sent
	 */
	public void sendMessage(MessageHeader msg) {
		try {
			out.writeObject(msg);
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while writing message to " + socket,name);
		}			
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