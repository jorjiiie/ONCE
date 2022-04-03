package ONCE.mining;

import ONCE.client.Client;
import ONCE.core.Block;
import ONCE.core.Transaction;
import ONCE.core.Logging;



// for testing

import ONCE.core.*;

import java.util.ArrayList;

public class MiningManager  {
	public static MiningManager self;

	Client host;
 	ArrayList<MiningThread> miners;
	Block currentBlock;
	public MiningManager(Client client) {
		miners = new ArrayList<MiningThread>();
		// limit to 3 for now
		host = client;
		self = this;
	}
	public void setBlock(Block b) {
		currentBlock = b;
		currentBlock.setBlockHeader();
	}
	public void addMiner(MiningThread thread) {
		miners.add(thread);
	}
	public void addMiners(int x) {
		for (int i=0;i<x;i++) {
			miners.add(new MiningThread(currentBlock.getBlockHeader(),i));
		}
	}

	public void updateBlock() {
		String header = currentBlock.setBlockHeader();
		for (MiningThread thread : miners) {
			thread.setBlock(header);
		}
	}

	public void resumeMining() {
		updateBlock();
		for (MiningThread thread : miners) {
			if (thread.isAlive() == false)
				thread.start();
		}
	}

	public void pauseMining() {
		Logging.log("Pausing");
		for (MiningThread thread : miners) {
			if (thread.isAlive() == true) 
				thread.interrupt();
		}
	}

	public void addTransaction(Transaction tx) {
		// add a hash - turn transaction into arraylist lmao

	}

	public void foundHash(byte[] hash, long nonce) {
		// hash is good

		currentBlock.setHash(hash);
		currentBlock.setSalt(nonce);
		// add this block!!!
		// host.addBlock(currentBlock);
		currentBlock.hash();
		Logging.log(currentBlock.toString());

		// pauseMining();
	}
	public static void main(String[] args) {
		RSA carl = new RSA();
		RSA joe = new RSA();

		Transaction[] ts = new Transaction[10];
		for (int i=0;i<10;i++) {
			ts[i] = new Transaction(carl.getPublic(), joe.getPublic(), 5);
			ts[i].sign(joe);
			System.out.println(ts[i].verify());
		}

		Block nb = new Block(ts, carl.getPublic());

		nb.setPrevious("0000000000000000000000000000000000000000000000000000000000000000");
		nb.hashTransactions();

		MiningManager manager = new MiningManager(null);
		manager.setBlock(nb);

		manager.addMiners(5);
		manager.resumeMining();
		Logging.log(nb.getBlockHeader());
		nb.hash();

		// manager.pauseMining();

		Logging.log(""+nb);


	}
}