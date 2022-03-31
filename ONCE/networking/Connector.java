package ONCE.networking;

import ONCE.core.*;
import ONCE.client.*;

import java.io.*;
import java.net.*;

/*
	this should just manage connections
	should not do much reading in/out though
	can pass the in/out into the other things (not even the sockets)


*/

/**
 * Manages connections for a client
 * @author jorjiiie
 */
public class Connector {

	static Connector self;
	Client host;
	ServerSocket server;
	int port;
	// have a reference to the client as well

	private NetManager manager;

	private boolean online = true;

	private ServerThread serverThread;

	/**
	 * Constructor that opens a server on addr:pt
	 * @param pt port to open on
	 * @param addr address to open on
	 */ 
	public Connector(int pt, InetAddress addr) {
		self = this;
		try {
			server = new ServerSocket(pt, 50, addr);
			port = pt;
			Logging.log("Opened server on port " + pt + " on address " + addr);
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Couldn't open port on port " + pt);
			return;
		}
		manager = new NetManager();
	}

	/**
	 * Begins listening for connections from other nodes
	 */
	public void listen() {
		serverThread = new ServerThread();
		serverThread.start();
	}

	// for the next three consider making manager package public...
	/**
	 * Broadcasts a message to all known connections
	 * @param msg message to broadcast
	 */
	public void broadcastMessage(MessageHeader msg) {
		manager.broadcastAll(msg);
		
	}

	/**
	 * Print current connections
	 */
	public void printConnections() {
		manager.printConnections();
	}

	/**
	 * Remove a connection
	 * @param np connection to remove
	 */
	public void removeConnection(NetPair np) {
		manager.removeConnection(np);
	}
	/**
	 * Connect to another node
	 * @param addr address to connect to
	 * @param port port to connect to
	 */
	public void connect(String addr, int port) {
		Logging.log("Connecting to " + addr + " on port " + port);

		// is a new thread necessary?
		// I don't think so
		new Thread() {
			public void run() {
				try {
					// check if already connected to this port
					NetPair np = manager.findConnection(addr, port);
					if (np != null) 
						return;

					NetSocket tmpBroadcaster = new NetSocket(addr, port);

					NetPair netPair = new NetPair(null, tmpBroadcaster);
					manager.addConnection(netPair);
					Logging.log("Broadcasting connection to " + addr + ":" +port);
					netPair.broadcaster.initIO();
					netPair.broadcaster.connect(server.getInetAddress(),server.getLocalPort());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Connect to another node
	 * @param addr address to connect to
	 * @param port port to connect to
	 */
	public void connect(InetAddress addr, int port) {

		Logging.log("Connecting to " + addr + " on port " + port);
		new Thread() {
			public void run() {
				try {
					NetPair np = manager.findConnection(addr, port);
					if (np != null)
						return;

					NetSocket tmpBroadcaster = new NetSocket(addr, port);

					NetPair netPair = new NetPair(null, tmpBroadcaster);
					manager.addConnection(netPair);
					tmpBroadcaster.initIO();
					tmpBroadcaster.connect(server.getInetAddress(),server.getLocalPort());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Shut down host connector and all associated connections
	 */
	public void shutdown() {
		Logging.log("Disconnecting from all connections");

		manager.shutdown();
	}

	/**
	 * ServerThread that handles new clients and will just spawn new threads
	 * Few threads are expected so creating new threads as opposed to a threadpool shouldn't be a problem'
	 */
	private class ServerThread extends Thread {

		@Override
		public void interrupt() {
			// stop connecting

		}

		public void run() {
			// do da loop
			Logging.log("Listening for connections on port " + port);

			while (online) {
				try {

 
					Socket soc = server.accept();

					// this is lazy? im assuming when this is interrupted these threads get destroyed as well
					// use a threadpool stupid
					new Thread() {
						public void run() {
							Logging.log("Recieved connection from " + soc);

							// soc will send a thing
							NetSocket tmpListener = new NetSocket(soc);
							tmpListener.initIO();

							MessageHeader initMessage = tmpListener.readMessage();
							if (initMessage != null && initMessage.type != 0) {
								// refuse connection or smth
								tmpListener.disconnect();
								return;
							}
							if (initMessage == null) {
								tmpListener.disconnect();
								return;
							}
							NetworkMessage info = (NetworkMessage) initMessage;

							Logging.log("Message recieved: " +info.addr + " " + info.port);

							if (manager.addListener(tmpListener, info.addr, info.port)) 
								return;

							NetSocket tmpBroadcaster = new NetSocket(info.addr, info.port);
							tmpBroadcaster.initIO();

							Logging.log("Sending connection to " + tmpBroadcaster.socket);

							tmpBroadcaster.connect(server.getInetAddress(), server.getLocalPort());

							NetPair netPair = new NetPair(tmpListener, tmpBroadcaster);
							tmpBroadcaster.pairUp(netPair);
							tmpListener.pairUp(netPair);

							manager.addConnection(netPair);

							Logging.log("Connected to " + tmpBroadcaster.socket);
							netPair.ready();
						}
					}.start();


				} catch (Exception e) {
					e.printStackTrace();

				}
			}

		}
	}
}
