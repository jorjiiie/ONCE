

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Master {
	public static Master self;

	public Networker slave;
	public ArrayList<Block> blockahs;

	public boolean stop;
	public Master() {
		self = this;
		blockahs = new ArrayList<Block>();

	}
	public void openPort(int port, InetAddress addr) {
		// will begin listening!!!
		slave = new Networker(port, addr);
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
	public static void main(String[] args) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName("127.0.0.1");
			System.out.println(addr);

		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("error localhost?");
			System.exit(0);
		}
		Master client = new Master();
		if (args.length > 0) {
			client.openPort(Integer.parseInt(args[0]), addr);
		} else {
			client.openPort(8069, addr);
		}
		client.slave.listen();
		// client.openPort(8069);
		client.randData(5);
		Scanner in = new Scanner(System.in);
		// just read for input lol and if we are already connected then whever lol
		new Thread() {
			public void run() {
				int nxt = 0;
				while (!client.stop) {
					System.out.println("1: Add new block\n2: Transmit data\n3: List connections\n4: Connect to another client\n-1: Shutdown");
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
						client.blockahs.add(b);
					} else if (nxt == 2) {
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
						//slave.transmit(client.blockahs.get(x));

					} else if (nxt == 3) {
						// see connections
						client.slave.printConnections();
					} else if (nxt == 4) {
						System.out.print("IP to connect to : ");
						in.nextLine();
						String nn = in.nextLine();
						System.out.print("Port: ");
						int port = in.nextInt();
						client.slave.connect(nn,port);
					} 
					else if (nxt == -1) {
						//client.slave.shutDown();
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