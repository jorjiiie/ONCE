package ONCE.mining;

import ONCE.client.Client;
import ONCE.core.Block;
import ONCE.core.Transaction;
import ONCE.core.Logging;



// for testing

import ONCE.core.*;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

public class MiningManager extends Thread {
	public static MiningManager self;

	Client host;

	Block currentBlock;
	private	boolean running = false;
	private boolean paused = false;
	private int num_threads;
	Thread[] miners;
	String header;

	public MiningManager(Client client) {
		// limit to 3 for now
		host = client;
		self = this;
		num_threads = 3;
		miners = new Thread[num_threads];
	}
	public MiningManager(Client client, int threads) {
		this(client);
		num_threads = threads;
		miners = new Thread[num_threads];
	}
	public void setBlock(Block b) {
		currentBlock = b;
		updateBlock();

	}

	public void updateBlock() {
		header = currentBlock.setBlockHeader();
	}

	// resume mining
	@Override
	public void run() {

		System.out.println("HI\n");
		running = true;
		paused = false;
		// if 
		while (running) {
			if (paused) {
				try {
					//sleep for a bit before checking for state again
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					Logging.log("Miners were not running, no nothing to pause");
					continue;
				}
			}
			else {
				updateBlock();
				FutureTask<?>[] tasks = new FutureTask[num_threads];
				// create new threads & start them off
				for (int i=0;i<num_threads;i++) {
					tasks[i] = new FutureTask<HashReturn>(new MiningTask(header, i));
					try {
						miners[i] = new Thread(tasks[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					miners[i].start();
				}

				// join them all
				for (int i=0;i<num_threads;i++) {
					try {
						// this stalls lmao but it's fine...
						// instead, we could have a constantly running task like the above
						// but then it would be much harder to return?
						// this is the best solution i have right now lol
						HashReturn ret = (HashReturn) tasks[i].get();
						System.out.println(ret.salt + " "  + ret.success);
					} catch (Exception e) {
						e.printStackTrace();
						Logging.log("no idea what happened but something is very wrong");
						return;
					}
				}


			}
		}
	}

	// maybe dont need this im confused
	@Override
	public void interrupt() {
		Logging.log("Shutting down miners");
		running = false;
	}

	@Override
	public void start() {
		Logging.log("STARTED\n");
		paused = false;
		running = true;
		super.start();
	}
	public void resumeMining() {
		// run if not already running
		updateBlock();

		paused = false;
	}

	public void pauseMining() {
		paused = true;
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

		host.addBlock(currentBlock);
		pauseMining();
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
		Logging.log("hi\n");
		manager.start();
		manager.resumeMining();
		Logging.log(nb.getBlockHeader());
		nb.hash();

		// manager.pauseMining();

		Logging.log(""+nb);


	}
}