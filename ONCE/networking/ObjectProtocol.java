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
	private boolean closed;

	public ObjectProtocol() {
		super();
	}
	public ObjectProtocol(InputStream _in, OutputStream _out) {
		super(_in, _out); 
		try {
			in = new ObjectInputStream(_in);
			out = new ObjectOutputStream(_out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public Message readMessage() {
		try {
			Object o = in.readObject();
			if (o instanceof Message) {
				// good
				return (Message) o;
			}
			Logging.log("Message object wasn't recieved");
			return null;
		} catch (Exception e) {
			if (!closed)
				e.printStackTrace();
		}
		return null;
	}
	public void writeMessage(Message msg) {
		try {
			out.writeObject(msg);
		} catch (Exception e) {
			if (!closed)
				e.printStackTrace();
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
				tmpListener.initIO();

				Message initMessage = tmpListener.readMessage();

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
	}

	/**
	 * Main method that runs a loop to listen and respond
	 */
	@Override
	public void run() {
		// while listening, run this loop
		while (true) {
			Message msg = readMessage();
			// switch on the msgheader
			MessageHeader header = msg.header;
			if (header == null) {
				// something is wrong with Message
				Logging.log("malformed message yo");
				continue;
			}
			switch (header.type) {

			}


		}
	}

	/**
	 * Interrupt the message writer
	 */
	@Override
	public void interrupt() {
		closed = true;
	}
}