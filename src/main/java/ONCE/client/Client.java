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

	// should likely move to their own class to make it more modular
	// private HashMap<String, Block> blockchain;
	Blockchain blockchain;

	private HashMap<BigInteger, Long> balances;
	// private HashSet<Block> blockchain;
	// inefficient lol
	private HashMap<String, Transaction> existingTransactions;
	// private NetworkBroadcaster broadcaster;


	// so we can update protocol
	public final int VERSION_NUMBER = 1;

	// LMAO
	public static final int MINING_DIFFICULTY = Block.MINING_DIFFICULTY;

	private Block lastBlock;
	private Connector connector;
	private MiningManager miningManager;
	private int currentDepth = 0;

	Block currentBlock;
	Block block2send = null;

	// probably an addblock method
	public Client() {
		hostClient = this;
		
		blockchain = new Blockchain();

		balances = new HashMap<BigInteger, Long>();

		miningManager = new MiningManager(this);
	}
	public static void test(Client client) {
		Scanner in = new Scanner(System.in);
		// just read for input lol and if we are already connected then whever lol
		new Thread() {
			public void run() {
				int nxt = 0;
				while (true) {
					System.out.println("1: Add new block\n2: Transmit data\n3: List connections\n4: Connect to another client\n5: Send message\n6: Display blocks\n7: start mining\n8: stop mining\n9: fabricate new transaction\n10: List working block\n-1: Shutdown");
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
						System.out.println("Hash of previous block:");
						in.nextLine();
						String str = in.nextLine();
						client.currentBlock.setPrevious(str);
						client.miningManager.setBlock(client.currentBlock);
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
						RSA fred = new RSA();
						Transaction ntx = new Transaction(joe.getPublic(), fred.getPublic(), amt);
						ntx.sign(fred);
						System.out.println(ntx.verify() + " this is pretty cash money???");
						client.addTransaction(ntx);

					} else if (nxt == 10) {
						client.miningManager.printWorkingBlock();
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
	public void addBlock(Block b) {

		// only propogate and add if not already there
		if (blockchain.queryBlock(b.getBlockHash()) == null) {
			// currentBlock = blockchain.addBlock(b);
			currentBlock.setPrevious(blockchain.addBlock(b).getBlockHash());
			// sendmessage
			BlockMessage bm = new BlockMessage(b);
			MessageHeader header = new MessageHeader(MessageHeader.BLOCK_MESSAGE, System.currentTimeMillis(), null);
			Message msg = new Message(header, bm);
			Logging.log("Broadcasting block that we just recieved");
			connector.broadcastMessage(msg);
			// should also set a new block to use
			miningManager.pauseMining();
			miningManager.setBlock(currentBlock);
			// miningManager.resumeMining();

		}

	}

	/**
	 * Defines behavior when a transaction is recieved from the network
	 * @param tx transaction to add
	 */
	public void addTransaction(Transaction tx) {
		miningManager.addTransaction(tx);
	}

	public int getDepth() {
		return currentDepth;
	}
	public void printBlocks() {
		blockchain.printBlocks();
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
		Client client = new Client();

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
		test(client);

		RSA carl = new RSA();
		RSA joe = new RSA();

		Transaction[] ts = new Transaction[10];
		for (int i=0;i<10;i++) {
			ts[i] = new Transaction(carl.getPublic(), joe.getPublic(), 5);
			ts[i].sign(joe);
			System.out.println(ts[i].verify());
		}

		Block nb = new Block(ts, carl.getPublic());

		nb.hashTransactions();
		nb.hash();
		client.currentBlock = nb;
		client.block2send = nb;
		client.miningManager.setBlock(nb);
		// client.addBlock(nb);
		// client.printBlocks();

	}
	
}
