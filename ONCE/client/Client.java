package ONCE.client;

import ONCE.networking.*;
import ONCE.core.*;
/*
 * Ryan Zhu
 * Client class. Keeps one main hashmap in memory (not disk lol! expecting blockchain to stay small) which is the blockchain (hash:block) itself, as well as transactions of users (hash:transaction, if one transaction == existing hash, auto reject) 
 * Also houses the threads used for networking and mining
 *
 */

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

// this is a MINING CLIENT (?)
// we can have non-mining clients that just broadcast transactions (no listeners, only some broadcasters)
public class Client {

	public static Client hostClient;

	// private HashMap<String, Block> blockchain;
	// private HashSet<Block> blockchain;
	// inefficient lol
	private HashMap<String, Transaction> existingTransactions;
	// private NetworkBroadcaster broadcaster;

	private ArrayList<MiningThread> miners;
	// so we can update protocol
	public final int VERSION_NUMBER = 0;


	// probably an addblock method
	public Client() {
		hostClient = this;
	}
	
}
