import java.io.*;
import java.net.*;

import java.util.ArrayList;


/**
 * 
 * Network Manager class that manages a collection of NetPairs, offering capabilities like adding and removing connections
 * as well as disconnecting or searching for a specific connection
 * @author jorjiiie 
 * 
 */
public class NetManager {

	// private ArrayList<NetPair> connections;

	/**
	 * No-args constructor that initializes the list
	 */ 
	public NetManager() {
		// connections = new ArrayList<NetPair>();
	}

	/**
	 * Adds a connection to the list, but checks if it already exists
	 * @param np is a NetPair that either is fully initialized or is half initialized with an open listener
	 */
	public void addConnection(NetPair np) {
	}
}