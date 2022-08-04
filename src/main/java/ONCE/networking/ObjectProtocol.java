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
				Message m = (Message) o;
				if (m.verify())
					return m;
				else 
					Logging.log("Checksum failed for message " + m);
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

	public static Message generateBlockMessage(Block b) {
		BlockMessage bm = new BlockMessage(b);

		MessageHeader header = new MessageHeader(MessageHeader.BLOCK_MESSAGE, System.currentTimeMillis(), bm.checksum());

		Message msg = new Message(header, bm);
		return msg;
	}
	public static Message generateTransactionMessage(Transaction tx) {
		TransactionMessage txMessage = new TransactionMessage(tx);

		MessageHeader header = new MessageHeader(MessageHeader.TRANSACTION_MESSAGE, System.currentTimeMillis(), txMessage.checksum());

		Message msg = new Message(header, txMessage);

		return msg;
	}
	public static Message generateNetworkMessage(InetAddress addr, int port) {
		NetworkMessage info = new NetworkMessage(addr, port, 1);

		MessageHeader header = new MessageHeader(MessageHeader.NETWORK_MESSAGE, System.currentTimeMillis(), info.checksum());

		Message msg = new Message(header, info);

		return msg;
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
		Message msg = generateNetworkMessage(addr, port);
		sendMessage(msg);

	}
	

	/**
	 * Main method that runs a loop to listen and respond
	 * 
	 */

	// i think instead there should be a loop in client and this just returns the message, as that makes it a lot nicer to work with
	// inside client, you have a loop that waits like 20 ms or something between checks & does like listener[i].checkavailable or something
	// or we can grab it with connector/network manager so we don't have to manage it and its not a pain
	// or another superclass that has a connector, and network manager
	// this also makes it easier to defer the things to networkmanager instead of through connector
	// or we would end up with two levels of deferring LOL and just call the getmessage from it :skull:
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
				case 3:
					BlockMessage bm = (BlockMessage) msg.data;
					Logging.log("Recieved block " + bm.block);
					Connector.self.host.addBlock(bm.block);
					break;
				case 4:
					TransactionMessage tm = (TransactionMessage) msg.data;
					Logging.log("Recieved transaction message " + tm.tx);
					Connector.self.host.addTransaction(tm.tx);

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