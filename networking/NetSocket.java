import java.io.*;
import java.net.*;

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