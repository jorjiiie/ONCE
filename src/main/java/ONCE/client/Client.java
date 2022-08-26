package ONCE.client;

import ONCE.networking.*;
import ONCE.networking.messages.*;
import ONCE.core.*;
import ONCE.mining.MiningManager;
/*
 * Ryan Zhu
 * Client class. Keeps one main hashmap in memory (not disk lol! expecting blockchain to stay small) which is the blockchain (hash:block) itself, as well as transactions of users (hash:transaction, if one transaction == existing hash, auto reject) 
 * Also houses the threads used for networking and mining
 *
 */
// make these imports specific
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigInteger;
import java.lang.Long;

// this is a MINING CLIENT (?)
// we can have non-mining clients that just broadcast transactions (no listeners, only some broadcasters)
public class Client {

	public static Client hostClient;
	private RSA user;

	// private	Blockchain blockchain;

	// so we can update protocol
	public final int VERSION_NUMBER = 1;

	private Block highestBlock;
	private Connector connector;
	private MiningManager miningManager;

	private Block currentBlock;

	// probably an addblock method
	public Client(RSA user) {

		this.user = user;

		hostClient = this;
		
		// blockchain = new Blockchain();


		miningManager = new MiningManager(this);
		miningManager.start();
	}
	public static void test(Client client) {
		Scanner in = new Scanner(System.in);
		// just read for input lol and if we are already connected then whever lol
		new Thread() {
			public void run() {
				int nxt = 0;
				while (true) {
					System.out.println("1: Add new block\n2: Transmit data\n3: List connections\n4: Connect to another client\n5: Send message\n6: Display blocks\n7: start mining\n8: stop mining\n9: fabricate new transaction\n10: List working block (have fun reading :sunglasses)\n11: Reset block\n12: Print balances (lol)\n13: super cool transactions\n-1: Shutdown");
					nxt = in.nextInt();
					if (nxt == 1) {
						// ????
					} else if (nxt == 2) {
						

					} else if (nxt == 3) {
						// see connections
						client.connector.printConnections();
					} else if (nxt == 4) {
						System.out.print("IP to connect to : ");
						in.nextLine();
						String nn = in.nextLine();
						System.out.print("Port: ");
						int port = in.nextInt();
						client.connector.connect(nn,port);


					} else if (nxt == 5) {
						in.nextLine();
						System.out.print("Message: ");
						String txt = in.nextLine();
						System.out.print("Author: " );
						String auth = in.nextLine();

						// this should be in object protocol, and should just have the interface
						// for handling different types of messages
						CommunicationMessage cm = new CommunicationMessage(txt,auth);
						MessageHeader header = new MessageHeader(MessageHeader.COMMUNICATION_MESSAGE, System.currentTimeMillis(), null);
						Message msg = new Message(header, cm);
						Logging.log("Boradcasting " + txt + " " + auth);
						client.connector.broadcastMessage(msg);

					} else if (nxt == 6) {
						// send the block
						Logging.log("Sending the block");
						BlockMessage bm = new BlockMessage(client.currentBlock);
						MessageHeader header = new MessageHeader(MessageHeader.BLOCK_MESSAGE, System.currentTimeMillis(), null);
						Message msg = new Message(header, bm);
						client.connector.broadcastMessage(msg);
					} else if (nxt == 7) {
						Logging.log("Starting miners");
						client.miningManager.resumeMining();

					} else if (nxt == 8) {
						Logging.log("Pausing miners");
						client.miningManager.pauseMining();
					} else if (nxt == 9) {
						// we only have two choices sooo
						System.out.print("quantity: ");
						long amt = in.nextLong();
						RSA joe = new RSA();
						Transaction ntx = new Transaction(joe.getPublic(), client.user.getPublic(), amt);
						ntx.sign(client.user);
						client.addTransaction(ntx);

					} else if (nxt == 10) {
						client.miningManager.printWorkingBlock();
					} else if (nxt == 11) {
						client.miningManager.resetBlock(1, client.user.getPublic(), Block.GENESIS_HASH);
					} else if (nxt == 12) {
						client.printBalances();
					} else if (nxt == 13) {
						System.out.print("quantity: ");
						long amt = in.nextLong();
						System.out.print("address: " );
						in.nextLine();
						String s = in.nextLine();
						BigInteger destination = new BigInteger(s,10);
						Transaction ntx = new Transaction(destination, client.user, amt);
						client.addTransaction(ntx);
					}
					else if (nxt == -1) {
						client.connector.shutdown();

						System.exit(0);
						break;
					} else 
						continue;
				}

			}
		}.start();
	}
	/**
	 * Resets the current block of the miner, and does associated work by also clearing blockchain temp transactions
	 * Sets the new block in miner to begin working as well
	 * @param depth depth of new block
	 * @param prev hash of previous block
	 */
	public void resetBlock(int depth, String prev) {
		// blockchain.resetWorkingTransactions();

		currentBlock = new Block(null, user.getPublic(), depth);
		currentBlock.setPrevious(highestBlock.getBlockHash());
		miningManager.pauseMining();
		miningManager.setBlock(currentBlock);

	}
	public void addBlock(Block b) {

		// only propogate and add if not already there

		/*
		if (blockchain.queryBlock(b.getBlockHash()) == null) {
			// we only add it is accepted!
			if (blockchain.testAdd(b)) {

				blockchain.addBlock(b);
				// make new block
				highestBlock = blockchain.getHighestBlock();
				Logging.log("Block accepted to blockchain");
				Logging.log("New highest block is " + highestBlock);

				// sendmessage
				Message msg = ObjectProtocol.generateBlockMessage(b);
				Logging.log("Broadcasting block that we just recieved");
				connector.broadcastMessage(msg);

				resetBlock(highestBlock.getDepth() + 1, highestBlock.getBlockHash());
			}
			
		}
		*/

	}

	/**
	 * Prints balances of blockchain (testing func)
	 */
	public void printBalances() {
		// blockchain.printBalances();
	}

	/**
	 * Defines behavior when a transaction is recieved from the network
	 * @param tx transaction to add
	 */
	public void addTransaction(Transaction tx) {
		/*
		if (blockchain.verifyWorkingTransaction(tx)) {

			blockchain.addWorkingTransaction(tx);

			miningManager.addTransaction(tx);

			Message msg = ObjectProtocol.generateTransactionMessage(tx);
			connector.broadcastMessage(msg);
		}
		*/

	}
	public void printBlocks() {
		// blockchain.printBlocks();
	}
	public static void main(String[] args) {
		InetAddress addr = null;
		try {
			// try localhost otherwise bc im lazy
			addr = InetAddress.getByName("127.0.0.1");
			System.out.println(addr);

		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("error finding to localhost");
			System.exit(0);
		}
		RSA carl = new RSA();
		RSA joe = new RSA();

		Client client = new Client(carl);

		int pt = 8069;
		if (args.length > 1) {
			try {
				addr = InetAddress.getByName(args[0]);
				System.out.println(addr);

			} catch (Exception e) {
				e.printStackTrace();
				Logging.log("error finding address " + args[0]);
				System.exit(0);
			}
			pt = Integer.parseInt(args[1]);
		} else if (args.length > 0) {
			pt = Integer.parseInt(args[0]);
		}
		client.connector = new Connector(pt, addr, client);
		client.connector.listen();
		Block genesis = Block.GENESIS_BLOCK;

		client.addBlock(genesis);

		test(client);
		Logging.log("Client: " + carl.getPublic());
		// client.addBlock(nb);
		// client.printBlocks();
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

	}
	
}
