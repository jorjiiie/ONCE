

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Host {
	public static Host self;

	public Connector connector;
	public ArrayList<Block> blockahs;

	public boolean stop;
	public Host() {
		blockahs = new ArrayList<Block>();

	}
	public void openPort(int port, InetAddress addr) {
		// will begin listening!!!
		connector = new Connector(port, addr);
	}

	public void connectPort(int port) {

	}
	public void randData(int n) {
		for (int i = 0; i < n; i++) {
			Apple[] apples = new Apple[(int)(Math.random() * 10) + 1];
			for (int j=0;j<apples.length;j++) {
				apples[j] = new Apple();
			}
			Block b = new Block(""+(char)('a'+i), apples.length, apples);
			blockahs.add(b);
		}
	}
	public void addBlock(Block b) {
		if (blockahs.contains(b))
			return;
		blockahs.add(b);
		BlockMessage m = new BlockMessage(b);
		connector.broadcastMessage(m);
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
		Host client = new Host();

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
		client.openPort(pt, addr);
		client.connector.listen();
		client.randData(5);

		Host.self = client;

		// System.out.println("ASDJHASHD " + Host.self.blockahs);

		Scanner in = new Scanner(System.in);
		// just read for input lol and if we are already connected then whever lol
		new Thread() {
			public void run() {
				int nxt = 0;
				while (!client.stop) {
					System.out.println("1: Add new block\n2: Transmit data\n3: List connections\n4: Connect to another client\n5: Send message\n6: Display blocks\n-1: Shutdown");
					nxt = in.nextInt();
					if (nxt == 1) {
						String n = in.nextLine();
						System.out.print("Name of the block: ");
						n = in.nextLine();
						int cnt = 0;
						System.out.print("Number of apples: " );
						cnt = in.nextInt();

						Apple[] apples = new Apple[cnt];
						in.nextLine();

						for (int i=0;i<cnt;i++) {
							System.out.print("For apple #" + (i+1) + ", name is: ");
							apples[i] = new Apple(in.nextLine());
						}

						Block b = new Block(n,cnt,apples);
						Logging.log("Added block: " + b);
						client.addBlock(b);

					} else if (nxt == 2) {
						// this is manual transmission so it would be when a  new node or smth joins?
						// idk man
						System.out.println("Currently contains " + client.blockahs.size() + " blocks:");
						for (int i=0;i<client.blockahs.size();i++) {
							System.out.println("#" + i + ": " + client.blockahs.get(i));
						}
						System.out.println("Choose one to send: ");
						int x = in.nextInt();
						if (x < 0 || x >= client.blockahs.size()) {
							System.out.println("calm down buckaroo");
							continue;
						}

						Logging.log("Attempting to transmit block " + client.blockahs.get(x));
						BlockMessage bm = new BlockMessage(client.blockahs.get(x));
						client.connector.broadcastMessage(bm);

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
						String msg = in.nextLine();
						System.out.print("Author: " );
						String auth = in.nextLine();
						CommunicationMessage cm = new CommunicationMessage(msg,auth);
						Logging.log("Boradcasting " + msg + " " + auth);
						client.connector.broadcastMessage(cm);

					} else if (nxt == 6) {
						// just print the stuff
						Logging.log("Currently contain: " + client.blockahs.size() + " blocks");
						for (Block b : client.blockahs) {
							Logging.log(b.toString());
						}
					}
					else if (nxt == -1) {
						client.connector.shutdown();
						// lol
						System.exit(0);
						break;
					} else 
						continue;
				}

			}
		}.start();
	}
}