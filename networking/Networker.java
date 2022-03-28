import java.io.*;
import java.net.*;

import java.util.ArrayList;


/*
	this should just manage connections
	should not do much reading in/out though
	can pass the in/out into the other things (not even the sockets)


*/
public class Networker {
	public static Networker self;
	public ServerSocket server;
	public int port;
	public ArrayList<NetPair> connections;

	public boolean online = true;

	public Networker(int pt, InetAddress addr) {
		// maybe just have this autoclose or run forever here??? 
		// have the listen thing here and then uhhhhhh the sockets also here? isnt that pure cancer
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
		Logging.log("Listening for connections on port " + port);
		new Thread() {
			public void run() {
				outer:
				while (online) {
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
		}.start();
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

	public class NetPair {
		public InetAddress addr;
		public int port;
		public NetSocket listener, broadcaster;

		public boolean disconnected;

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
				Networker.self.remove(this);
				return;
			}

			listener.disconnect();
			broadcaster.disconnectWithMessage();

			disconnected = true;

			Networker.self.remove(this);

			Logging.log("Successfully disconnected from " + addr + ":" + port);
		}
		public void disconnectNoMessage() {

			if (disconnected)
				return;

			listener.disconnect();
			broadcaster.disconnect();

			disconnected = true;

			Networker.self.remove(this);

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
	// instead of two classes you could just have one networksocket class
	// and call them listener/broadcaster...
	public class NetSocket {
		public NetPair parent;
		public Socket socket;
		
		public ObjectOutputStream out;
		public ObjectInputStream in;

		public boolean ready = false;
		public String name = "DEFAULT SOCKET";
		public NetSocket(Socket soc) {
			socket = soc;
		}
		public NetSocket(InetAddress addr, int port) {
			try {
				socket = new Socket(addr, port);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't connect to " + addr + " on port " + port, name);
			}
		}
		public NetSocket(String addr, int port) {
			try {
				socket = new Socket(addr, port);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't connect to " + addr + " on port " + port, name);
			}
		}
		public void initIO() {
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
	            in = new ObjectInputStream(socket.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Couldn't open io streams of " + socket, name);
			}
		}
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
		public void connect(InetAddress addr, int port) {
			// sends a connection message w the info of this stuff
			NetworkMessage msg = new NetworkMessage(addr, port);
			// write the message to out
			sendMessage(msg);
		}

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
		public void startListening() {
			// return;
			
			Logging.log("Started to listen from " + socket,name);
			// this should be interruptable or you can just catch the close exception as shut down connection
			// run a protocol in a new thread? that gets the netpair 

			// needs a new thread so we can interrupt
			new Thread() {
				public void run() {
					Logging.log(socket + " is " + (ready?"ready":"not ready"));
					while (ready) {
						try {
							MessageHeader m = (MessageHeader) in.readObject();
							if (m.type == -1) {
								// disconnect message
								Logging.log("Recieved disconnect message from " + socket + ", shutting down");
								parent.disconnectNoMessage();
								return;
							} else if (m.type == 1) {
								continue;
							} else if (m.type == 2) {
								BlockMessage bm = (BlockMessage) m;
								Logging.log("Recieved block " + bm.block + " from " + socket);
								NodeHost.self.addBlock(bm.block);
							} else if (m.type == 3) {
								// say something message
								CommunicationMessage cm = (CommunicationMessage) m;
								Logging.log("Recieved: " + cm.message + " from: " +cm.author);
							}
						} catch (IOException e) {
							// server shut down

							// don't need this bc this is only if shut down
							// e.printStackTrace();
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
		public void sendMessage(MessageHeader m) {
			try {
				out.writeObject(m);
			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("Error while writing message to " + socket,name);
			}			
		}

		public void setName(String s) {
			name = s;
		}
		public void pairUp(NetPair np) {
			parent = np;
		}

	}
}