import java.io.*;
import java.net.*;

import java.util.ArrayList;


/*
	this should just manage connections
	should not do much reading in/out though
	can pass the in/out into the other things (not even the sockets)


*/
public class Connector {
	static Connector self;
	ServerSocket server;
	int port;
	// could have net manager as a class...
	private ArrayList<NetPair> connections;

	private NetManager manager;

	private boolean online = true;

	private ServerThread serverThread;

	public Connector(int pt, InetAddress addr) {
		self = this;
		try {
			server = new ServerSocket(pt, 50, addr);
			port = pt;
			Logging.log("Opened server on port " + pt + " on address " + addr);
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Couldn't open port on port " + pt);
		}
		connections = new ArrayList<NetPair>();
	}

	public void listen() {
		serverThread = new ServerThread(this);
		serverThread.start();
	}
	public void broadcastMessage(MessageHeader m) {
		Logging.log("Broadcasting " + m);
		for (NetPair np : connections) {
			if (!np.broadcaster.socket.isClosed())
				np.broadcaster.sendMessage(m);
		}
	}

	public void printConnections() {
		Logging.log("System contains " + connections.size() + " connections:");
		for (NetPair np : connections) {
			System.out.println(np.addr + " " + np.port + " " + np.broadcaster.socket + " " + np.listener.socket + " closed: " + np.broadcaster.socket.isClosed() + " " + np.listener.socket.isClosed());
		}
	}
	public void connect(String host, int port) {
		Logging.log("Connecting to " + host + " on port " + port);

		new Thread() {
			public void run() {
				try {
					// check if already connected to this port
					for (NetPair np : connections) {
						if (np.addr.getHostAddress().equals(host) && port == np.port) {
							Logging.log("Error: Already connected to " + host + ":" + np.port);
							return;
						}
					}

					NetSocket tmpBroadcaster = new NetSocket(host, port);

					NetPair netPair = new NetPair(null, tmpBroadcaster);
					connections.add(netPair);
					Logging.log("Broadcasting connection to " + host + ":" +port);
					netPair.broadcaster.initIO();
					netPair.broadcaster.connect(server.getInetAddress(),server.getLocalPort());
					// netPair.checkState();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void connect(InetAddress addr, int port) {
		Logging.log("Connecting to " + addr + " on port " + port);
		new Thread() {
			public void run() {
				try {
					for (NetPair np : connections) {
						if (np.addr.equals(addr) && port == np.port) {
							Logging.log("Error: Already connected to " + addr + ":" + np.port);
							return;
						}
					}
					NetSocket tmpBroadcaster = new NetSocket(addr, port);

					NetPair netPair = new NetPair(null, tmpBroadcaster);
					connections.add(netPair);
					tmpBroadcaster.initIO();
					tmpBroadcaster.connect(server.getInetAddress(),server.getLocalPort());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void shutdown() {
		// shut down all connections
		Logging.log("Disconnecting from all connections");

		_online = false;
		// issue is that this thing removes while going through
		// if u use a lock itll deadlock stupid
		for (NetPair np : connections) {
			np.shutdown();
		}
	}

	public void remove(NetPair np) {
		// should be a lock so it doesnt mess up
		if (np.disconnected) {
			connections.remove(np);
		}
	}

		
	private class ServerThread extends Thread {
		private Connector parent;
		public ServerThread(Connector _parent) {
			parent = _parent;
		}
		@Override
		public void Interrupt() {

		}

		public void run() {
			// do da loop
			Logging.log("Listening for connections on port " + port);

			outer:
			while (_parent.online) {
				try {

 
					Socket soc = server.accept();

					// start a new thread here :skull:
					Logging.log("Recieved connection from " + soc);

					// soc will send a thing
					NetSocket tmpListener = new NetSocket(soc);
					tmpListener.initIO();
					// maybe we could spin a thread here but honestly itll send the init message 
					// should spin a new thread here

					// maybe just pass it into the listening thread here so it doesnt get corrupted or whatever is happening
					// if it doesn't find a match, then it adds itself to the connections list 
					// then it goes on to listen to whatever it was doing before?
					MessageHeader initMessage = tmpListener.readMessage();
					if (initMessage != null && initMessage.type != 0) {
						// refuse connection or smth
						tmpListener.disconnect();
						continue;
					}
					if (initMessage == null) {
						tmpListener.disconnect();
						continue;
					}
					NetworkMessage info = (NetworkMessage) initMessage;

					Logging.log("Message recieved: " +info.addr + " " + info.port);
					for (NetPair np : connections) {
						// System.out.println("ABCBBC" + np.addr + " " + info.addr + " " + np.port + " " + info.port);
						if (np.addr.equals(info.addr) && np.port == info.port) {
							// set the listener pair to this and then move on, as it can be the incoming connection from a connection we established
							Logging.log("Found connection match");
							if (np.listener != null)  {
								Logging.log("Already connected to " + info.addr + ":" + info.port);
								continue outer;	
								
							}
							tmpListener.parent = np;
							np.listener = tmpListener;
							np.ready();
							tmpListener.startListening();
							continue outer;
						}
					}
					
					// otherwise, this is a new connection
					// make the pair

					NetSocket tmpBroadcaster = new NetSocket(info.addr, info.port);
					tmpBroadcaster.initIO();

					// i have no idea what this is for
					/*
					if (connections.size() > 0) {
						continue;
					}
					*/

					Logging.log("Sending connection to " + tmpBroadcaster.socket);

					tmpBroadcaster.connect(server.getInetAddress(), server.getLocalPort());

					NetPair netPair = new NetPair(tmpListener, tmpBroadcaster);
					tmpBroadcaster.pairUp(netPair);
					tmpListener.pairUp(netPair);
					connections.add(netPair);
					Logging.log("Connected to " + tmpBroadcaster.socket);
					netPair.ready();
					tmpListener.startListening();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
}