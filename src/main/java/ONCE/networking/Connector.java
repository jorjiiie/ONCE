package ONCE.networking;

import ONCE.core.*;
import ONCE.client.*;
import ONCE.networking.messages.*;

import java.io.*;
import java.net.*;

/*
	this should just manage connections
	should not do much reading in/out though
	can pass the in/out into the other things (not even the sockets)


*/

/**
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
	public Connector(int pt, InetAddress addr, Client client) {
		self = this;
		host = client;
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
	// i think we can have netmanager AND connector be in host, and the connector can return its netmanager for
	// host to use some time
	/**
	 * 
	 * Broadcasts a message to all known connections
	 * @param msg message to broadcast
	 */
	public void broadcastMessage(Message msg) {
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

		new Thread() {
			public void run() {
				try {
					if (manager.findConnection(addr, port) != null) 
						return;

					NetSocket tmpBroadcaster = new NetSocket(addr, port);

					NetPair netPair = new NetPair(null, tmpBroadcaster);
					manager.addConnection(netPair);
					Logging.log("Broadcasting connection to " + addr + ":" +port);
					netPair.broadcaster.initProtocol();
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
					Logging.log("Broadcasting connection to " + addr + ":" +port);
					tmpBroadcaster.initProtocol();
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

					Protocol connect = new ObjectProtocol();

					connect.onConnect(manager,soc,server);


				} catch (Exception e) {
					e.printStackTrace();

				}
			}

		}
	}
}
