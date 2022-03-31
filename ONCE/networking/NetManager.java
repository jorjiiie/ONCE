package ONCE.networking;

import ONCE.core.*;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

/**
 * 
 * Network Manager class that manages a collection of NetPairs, offering capabilities like adding and removing connections
 * as well as disconnecting or searching for a specific connection
 * @author jorjiiie 
 * 
 */
public class NetManager {

	// could implement as a hashset with hashcode = hashcode (addr) + hashcode (port)
	private ArrayList<NetPair> connections;
	private ReentrantReadWriteLock lock;
	private Lock readLock, writeLock;
	/**
	 * No-args constructor that initializes the list and read/write locks to ensure that the list is not being
	 * written to at the wrong times
	 */ 
	public NetManager() {
		connections = new ArrayList<NetPair>();
		lock = new ReentrantReadWriteLock();

		readLock = lock.readLock();
		writeLock = lock.readLock();
	}

	/**
	 * Adds a connection to the list, but checks if it already exists
	 * @param np is a NetPair that either is fully initialized or is half initialized with an open listener
	 * @return true if the connection was added, false if it was already found
	 */
	public boolean addConnection(NetPair np) {
		NetPair _np = findConnection(np.addr, np.port);
		if (_np != null) {
			Logging.log("Already connected to " + np.addr + ":" + np.port);
			return false;
		}
		try {
			writeLock.lock();
			connections.add(np);
		} finally {
			writeLock.unlock();
		}

		return true;
	}

	/**
	 * Removes a connection from the list if it exists
	 * @param np connection to remove
	 */
	public void removeConnection(NetPair np) {
		try {
			writeLock.lock();
			connections.remove(np);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Adds a listener to an existing NetPair if it exists, or doesn't add if the listener is already present
	 * @param listener listener to be added
	 * @param addr Address to connect to
	 * @param port Port to connect to
	 * @return true if an existing NetPair is found, false if none
	 */
	public boolean addListener(NetSocket listener, InetAddress addr, int port) {
		NetPair np = findConnection(addr, port);
		if (np == null)
			return false;

		// technically write lock? I don't think it's necessary
		if (np.listener == null) {
				np.listener = listener;
				listener.pairUp(np);

				np.ready();

		} else 
			Logging.log("Already connected to " + addr + ":" + port);
		return true;

	}

	/**
	 * Finds a connection within current connections
	 * @param addr Address to connect to
	 * @param port Port to connect to
	 * @return the found NetPair or null
	 */
	public NetPair findConnection(InetAddress addr, int port) {
		try {
			readLock.lock();
			for (NetPair np : connections) {
				if (np.addr.equals(addr) && np.port == port)
					return np;
			}

		} finally {
			readLock.unlock();
		}
		return null;
	}

	/**
	 * Finds a connection within current connections
	 * @param addr Address to connect to 
	 * @param port Port to connect to
	 * @return the found NetPair or null
	 */
	public NetPair findConnection(String addr, int port) {
		try {
			readLock.lock();
			for (NetPair np : connections) {
				if (np.addr.getHostAddress().equals(addr) && np.port == port) 
					return np;
			}
		} finally {
			readLock.unlock();
		}
		return null;
	}

	/**
	 * Broadcasts a message to all known connections
	 * @param msg Message to be broadcasted
	 */
	public void broadcastAll(MessageHeader msg) {
		Logging.log("Broadcasting " + msg);
		try {
			readLock.lock();
			for (NetPair np : connections) {
				// consider having just a np method vs a np.broadcaster call
				if (!np.broadcaster.socket.isClosed())
					np.broadcaster.sendMessage(msg);
			}
		} finally {
			readLock.unlock();
		}

	}

	/**
	 * Shuts down all connections
	 * Waits for some stuff to finish (anything currently operating on this) but doesn't wait for np to finish their current operation
	 */
	public void shutdown() {
		try {
			writeLock.lock();
			for (NetPair np : connections) {
				np.shutdown();
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Prints out connections and their related information
	 */
	public void printConnections() {
		Logging.log("Currently connected to " + connections.size() + " nodes");
		try {
			readLock.lock();

			int connectionCount = 1;
			for (NetPair np : connections) {
				Logging.log("Connection " + connectionCount + ": " + np.getInfo());
				connectionCount++;
			}
		} finally {
			readLock.unlock();
		}
	}
}