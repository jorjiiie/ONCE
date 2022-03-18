package ONCE.networking;

import ONCE.client.*;

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
	private static int NUMBER_LISTENERS = 8000;

	private ServerSocket server;
	private HashSet<Listener> listeners;

	// for grabbing data from client
	Client hostClient;

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
				try (
					Socket client = server.accept();
				) {
					listeners.add(new Listener(client));
				} catch(IOException e) {
					e.printStackTrace();
				}

			}
		};
		clientListener.start();

	}


	private class Listener extends Thread {
		// each has threadpool that takes out the tasks
		// these are the LISTENERS so these will send stuff back 
		private Socket client;

		public Listener(Socket _client) {
			client = _client;
		}
		public void run() {
			// listen to the client and accept stuff from it?
		}
	}
}
