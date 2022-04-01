package ONCE.networking;

import ONCE.networking.*;
import ONCE.networking.messages.*;
import ONCE.core.*;

import java.io.*;
import java.net.*;

/**
 * Java-only protocol that is for testing and me being lazy
 * @author jorjiiie
 */
public class ObjectProtocol extends Protocol {
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ObjectProtocol() {
		super();
	}
	public ObjectProtocol(NetSocket soc) {
		super(soc); 
		try {
			out = new ObjectOutputStream(soc.socket.getOutputStream());
			in = new ObjectInputStream(soc.socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while opening input streams");
			// need to have some way to catch this back in socket
		}
		
	}
	public Message readMessage() {
		if (closed)
			return null;
		try {
			Object o = in.readObject();
			if (o instanceof Message) {
				// good
				return (Message) o;
			}
			Logging.log("Message object wasn't recieved");
		} catch (SocketException e) {
			closed = true;
		} catch (EOFException e) {
			closed = true;
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while reading message");
		}
		return null;
	}

	public void sendMessage(Message msg) {
		if (closed)
			return;
		try {
			out.writeObject(msg);
		} catch (SocketException e) {
			closed = true;
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error while writing message");
		}
	}
	public void onConnect(NetManager manager, Socket soc, ServerSocket server) {
		// just do a thing on connect...
		new Thread() {
			public void run() {

				Logging.log("Recieved connection from " + soc);

				// connect.onConnect(manager, self);

				// soc will send a thing
				NetSocket tmpListener = new NetSocket(soc);
				tmpListener.initProtocol();

				Message initMessage = tmpListener.proto.readMessage();

				if (initMessage != null && initMessage.header.type != 1) {
					// refuse connection or smth
					tmpListener.disconnect();
					return;
				}
				if (initMessage == null) {
					tmpListener.disconnect();
					return;
				}

				NetworkMessage info = (NetworkMessage) initMessage.data;

				Logging.log("Message recieved: " +info.addr + " " + info.port);

				if (manager.addListener(tmpListener, info.addr, info.port)) 
					return;

				NetSocket tmpBroadcaster = new NetSocket(info.addr, info.port);
				tmpBroadcaster.initProtocol();

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
	}
	public void connect(InetAddress addr, int port) {
		NetworkMessage info = new NetworkMessage(addr, port, 1);

		MessageHeader header = new MessageHeader(MessageHeader.NETWORK_MESSAGE, System.currentTimeMillis(), info.checksum());

		Message msg = new Message(header, info);

		sendMessage(msg);
	}

	/**
	 * Main method that runs a loop to listen and respond
	 */
	@Override
	public void run() {
		// while listening, run this loop
		while (!closed) {
			Message msg = readMessage();

			if (closed)
				return;

			if (msg == null) 
				continue;
			
			// switch on the msgheader
			MessageHeader header = msg.header;
			if (header == null) {
				// something is wrong with Message
				Logging.log("malformed message yo");
				continue;
			}
			switch (header.type) {
				case -2:
					closed = true;
					break;
				case -1:
					// uhh rejected idfk what this means
					break;
				case 1:
					Logging.log("Recieved network message");
					break;
				case 2:
					CommunicationMessage cm = (CommunicationMessage) msg.data;
					Logging.log("Recieved message: " + cm.message + " from " + cm.author);
					break;

			}
		}
		socket.parent.disconnectNoMessage();
	}

	/**
	 * Interrupt the protocol (the run method)
	 */
	@Override
	public void interrupt() {
		// socket will be closed from the outside
		closed = true;
	}
}