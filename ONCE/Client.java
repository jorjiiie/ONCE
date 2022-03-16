/*
 * Ryan Zhu
 * Client class. Keeps one main hashmap in memory (not disk lol! expecting blockchain to stay small) which is the blockchain (hash:block) itself, as well as transactions of users (hash:transaction, if one transaction == existing hash, auto reject) 
 * Also houses the threads used for networking and mining
 *
 */


import java.util.HashMap;



public class Client {
	private HashMap<String, Block> blockchain;
	// inefficient lol
	private HashMap<String, Transaction> existingTransactions;

	
	
}