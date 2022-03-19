package ONCE.networking;

import ONCE.client.*;
import ONCE.core.*;

import java.net.*;
import java.io.*;
import java.util.HashSet;
/*
 * Network Listener class
 * Ryan Zhu
 * March 2022
 * Accepts new connections and manages them
 *
 */

public class NetworkListener extends Thread{
	// for local testing to host multiple nodes on one computer
	// am assuming that there will not be clashing nodes at the exact same time lol
	// change this to a pass in args thing in the main client
	private static int NUMBER_LISTENERS = 8000;

	private ServerSocket server;
	private HashSet<Listener> listeners;

	public NetworkListener() {
		// this will pick first available port
		try	{
			server = new ServerSocket(NUMBER_LISTENERS++);

			System.out.println("Listening on port " + server.getLocalPort());
			listeners = new HashSet<Listener>();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// also add the unknown connection error
	}

	public void run() {
		// add and run
		// will need a mechanism to clear out, so maybe a hashmap?
		try (
			Socket client = server.accept();
		) {
			listeners.add(new Listener(client));
		} catch(IOException e) {
			e.printStackTrace();
		}
		// hashmap addr to listener maybe
	}

	public void initListener() {
		// somehow implement so that this thread can access listeners
		// i don't really want to implement a new thread but that is the last resort 
		Thread clientListener = new Thread() 
		{
			public void run() {
				while (true) {
					try (
						Socket client = server.accept();
					) {
						Listener current = new Listener(client);
						listeners.add(current);
						current.start();
						Logging.log("Connected to " + client + " and is now listening");
						// can stop the listener by closing the client socket
					} catch(IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
		clientListener.start();
	}
	public void closeListener(Socket s) {
		// we will just throw around references of socket like willy-nilly

	}

}
