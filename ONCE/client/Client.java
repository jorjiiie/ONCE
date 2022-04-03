package ONCE.client;

import ONCE.networking.*;
import ONCE.networking.messages.*;
import ONCE.core.*;
/*
 * Ryan Zhu
 * Client class. Keeps one main hashmap in memory (not disk lol! expecting blockchain to stay small) which is the blockchain (hash:block) itself, as well as transactions of users (hash:transaction, if one transaction == existing hash, auto reject) 
 * Also houses the threads used for networking and mining
 *
 */
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
	private HashMap<String, Block> blockchain;
	private HashMap<BigInteger, Long> balances;
	// private HashSet<Block> blockchain;
	// inefficient lol
	private HashMap<String, Transaction> existingTransactions;
	// private NetworkBroadcaster broadcaster;

	private ArrayList<MiningThread> miners;
	// so we can update protocol
	public final int VERSION_NUMBER = 0;

	private Connector connector;

	Block block2send = null;

	// probably an addblock method
	public Client() {
		hostClient = this;
		
		blockchain = new HashMap<String, Block>();

		balances = new HashMap<BigInteger, Long>();
	}
	public static void test(Client client) {
		Scanner in = new Scanner(System.in);
		// just read for input lol and if we are already connected then whever lol
		new Thread() {
			public void run() {
				int nxt = 0;
				while (true) {
					System.out.println("1: Add new block\n2: Transmit data\n3: List connections\n4: Connect to another client\n5: Send message\n6: Display blocks\n-1: Shutdown");
					nxt = in.nextInt();
					if (nxt == 1) {
						
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
						BlockMessage bm = new BlockMessage(client.block2send);
						MessageHeader header = new MessageHeader(MessageHeader.BLOCK_MESSAGE, System.currentTimeMillis(), null);
						Message msg = new Message(header, bm);
						client.connector.broadcastMessage(msg);
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

	public boolean addBlock(Block b) {
		Logging.log("hi");
		String prevBlock = b.getPrevious();

		/*
		if (blockchain.isEmpty() == false) {
			if (prevBlock == null) {
					return false;
			}
			if (blockchain.get(prevBlock) == null) {
				return false;
			}
		}
		*/

		
		// validate block
		if (b.verify() == false) {
			return false;
		}
		blockchain.put(b.getBlockHash(), b);
		return true;
	}

	public void printBlocks() {
		for (Block b : blockchain.values()) {
			Logging.log(b.toString());
		}
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

		client.block2send = nb;
		client.addBlock(nb);
		client.printBlocks();

	}
	
}
