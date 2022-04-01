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

	private Connector connector;

	// probably an addblock method
	public Client() {
		hostClient = this;
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
						CommunicationMessage cm = new CommunicationMessage(txt,auth);
						MessageHeader header = new MessageHeader(MessageHeader.COMMUNICATION_MESSAGE, System.currentTimeMillis(), null);
						Message msg = new Message(header, cm);
						Logging.log("Boradcasting " + txt + " " + auth);
						client.connector.broadcastMessage(msg);

					} else if (nxt == 6) {

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
		client.connector = new Connector(pt, addr);
		client.connector.listen();
		test(client);
	}
	
}
