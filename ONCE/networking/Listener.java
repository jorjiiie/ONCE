package ONCE.networking;

import ONCE.core.*;
import ONCE.client.*;

import java.io.*;
import java.net.*;
// these have to only connect to one, and each broadcaster should only connect to one client or else itll fuck up the buffers
// 


public class Listener extends Thread {
	// these are the LISTENERS so these will send stuff back 
	// or listeners have threads that a) read and b) pingpong everything
	// pingpong is for broadcasting lol but yeah listeners i think we only need a single thread per client!
	// broadcasting maybe a threadpool

	
	private Socket client;
	// OR JUST WRITE OBJECTS LIKE
	// HEADER, BODY, ETC ETC!!!! LETS GO?
	private DataInputStream in;
	private DataOutputStream out;
	public Listener(Socket _client) {
		client = _client;
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		// interrupt the reading and close the listener
		try {
			client.shutdownInput();
			client.shutdownOutput();
			client.close();
			Logging.log("Shut down socket " + client);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			super.interrupt();
		}
	}

	public void run() {
		// listen to the client and accept stuff from it?
		String inp = "";

		while (true) {
			// uhh i dont know run this entirely on the protocol?
			// should make this in parallel with the broadcaster
			ONCEProtocol hostProtocol = new ONCEProtocol(true);
			
			break;
		}
		Logging.log("Socket " + client + " closed");

	}
}