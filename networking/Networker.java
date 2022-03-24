import java.io.*;
import java.net.*;

import java.util.ArrayList;

public class Networker {
	public ServerSocket server;
	public int port;
	public ArrayList<NetPair> connections;

	public boolean online = true;
	public Networker(int pt, InetAddress addr) {
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
		Logging.log("Listening for connections on port " + port);
		new Thread() {
			public void run() {
				outer:
				while (online) {
					try (
						Socket soc = server.accept();
					) {
						Logging.log("Established connection with " + soc);
						// soc will send a thing
						NetworkListener tmpListener = new NetworkListener(soc);
						tmpListener.initIO();
						// maybe we could spin a thread here but honestly itll send the init message 
						MessageHeader initMessage = tmpListener.readMessage();
						if (initMessage != null && initMessage.type != 0) {
							// refuse connection or smth
							tmpListener.disconnect();
							continue;
						}
						NetworkMessage info = (NetworkMessage) initMessage;
						for (NetPair np : connections) {
							// System.out.println("ABCBBC" + np.addr + " " + info.addr + " " + np.port + " " + info.port);
							if (np.addr.equals(info.addr) && np.port == info.port) {
								// set the listener pair to this and then move on, as it can be the incoming connection from a connection we established
								np.listener = tmpListener;
								continue outer;
							}
						}
						// otherwise, this is a new connection
						// make the pair
						NetworkBroadcaster tmpBroadcaster = new NetworkBroadcaster(info.addr, info.port);
						tmpBroadcaster.initIO();
						if (connections.size() > 0) {
							continue;
						}
						tmpBroadcaster.connect(server.getInetAddress(), server.getLocalPort());

						NetPair netPair = new NetPair(tmpListener, tmpBroadcaster);
						tmpBroadcaster.pairUp(netPair);
						tmpListener.pairUp(netPair);
						connections.add(netPair);
						Logging.log("Connected to " + tmpBroadcaster.host);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}
	public void printConnections() {
		Logging.log("System contains " + connections.size() + " connections:");
		for (NetPair np : connections) {
			System.out.println(np.addr + " " + np.port + " " + np.broadcaster.host + " " + np.listener.client);
		}
	}
	public void connect(String host, int port) {
		Logging.log("Connecting to " + host + " on port " + port);

		new Thread() {
			public void run() {
				try {
					// check if already connected to this port
					for (NetPair np : connections) {
						System.out.println(np.addr.getHostAddress() + " " + host);
						if (np.addr.getHostAddress().equals(host) && port == np.port) {
							Logging.log("Error: Already connected to " + host + ":" + np.port);
							return;
						}
					}

					System.out.println("hi");
					NetworkBroadcaster tmpBroadcaster = new NetworkBroadcaster(host, port);
					NetPair netPair = new NetPair(null, tmpBroadcaster);
					connections.add(netPair);
					Logging.log("Created new broadcasting connection to " + host + " : " +port);
					tmpBroadcaster.initIO();
					System.out.println("server: " + server.getInetAddress() + " " + server.getLocalPort());
					tmpBroadcaster.connect(server.getInetAddress(),server.getLocalPort());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		System.out.println("bacK");
	}
	public void connect(InetAddress addr, int port) {
		Logging.log("Connecting to " + addr + " on port " + port);
		new Thread() {
			public void run() {
				try {
					NetworkBroadcaster tmpBroadcaster = new NetworkBroadcaster(addr, port);

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
	}

	public class NetPair {
		public InetAddress addr;
		public int port;
		public NetworkListener listener;
		public NetworkBroadcaster broadcaster;
		public NetPair(NetworkListener _listener, NetworkBroadcaster _broadcaster) {
			listener = _listener;
			broadcaster = _broadcaster;
			// broadcaster is always defined, so we can grab the address from that
			addr = _broadcaster.host.getInetAddress();
			port = _broadcaster.host.getPort();
		}
	}
	// instead of two classes you could just have one networksocket class
	// and call them listener/broadcaster...
	public class NetworkListener {

		public NetPair parent;
		public Socket client;

	    public ObjectOutputStream out;
	    public ObjectInputStream in;

		public NetworkListener(Socket soc) {
			client = soc;
		}
		public NetworkListener(InetAddress addr, int port) {
			try {
				client = new Socket(addr, port);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't connect to " + addr + " on port " + port, "LISTENER");
			}
		}
		public void initIO() {
			try {
				out = new ObjectOutputStream(client.getOutputStream());
	            in = new ObjectInputStream(client.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't open io streams of " + client, "LISTENER");
			}
		}
		// these should go in the protocol
		// protocol should have an on-connect
		// bc if its here its harder to change?
		public MessageHeader readMessage() {
			try {
				MessageHeader msg = (MessageHeader) in.readObject();
				return msg;
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Error while reading message from " + client);
				return null;
			}

		}
		public void disconnect() {
			try {
				client.shutdownInput();
				client.shutdownOutput();
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Error while closing connection to " + client, "LISTENER");
			}
		}
		public void pairUp(NetPair np) {
			parent = np;
		}

	}
	public class NetworkBroadcaster {
		public NetPair parent;
		public Socket host;
	    public ObjectOutputStream out;
	    public ObjectInputStream in;
		public NetworkBroadcaster(InetAddress addr, int port) {
			try {
				host = new Socket(addr, port);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't connect to " + addr + " on port " + port, "BROADCASTER");
			}
		}
		public NetworkBroadcaster(String addr, int port) {
			try {
				host = new Socket(addr, port);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't connect to " + addr + " on port " + port, "BROADCASTER");
			}
		}
		public void initIO() {
			try {
				out = new ObjectOutputStream(host.getOutputStream());
	            in = new ObjectInputStream(host.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't open io streams of " + host, "BROADCASTER");
			}
		}
		public void sendMessage(MessageHeader m) {
			try {
				out.writeObject(m);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't write object to " + host);
			}
		}
		public void connect(InetAddress addr, int port) {
			// sends a connection message w the info of this stuff
			NetworkMessage msg = new NetworkMessage(addr, port);
			// write the message to out
			sendMessage(msg);
		}
		public void disconnect() {
			// send disconnect, then disconnect
			MessageHeader m = new MessageHeader(-1);
			sendMessage(m);
			try {
				host.shutdownInput();
				host.shutdownOutput();
				host.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Error while closing connection to " + host, "BROADCASTER");
			}

		}
		public void pairUp(NetPair np) {
			parent = np;
		}
	}
}