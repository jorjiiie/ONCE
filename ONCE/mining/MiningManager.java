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
	// should be atomicboolean
	private boolean paused = false;
	private boolean firstPause = true;
	private int num_threads;
	Thread[] miners;
	String header;
	FutureTask<?>[] tasks;

	public MiningManager(Client client) {
		// limit to 3 for now
		host = client;
		self = this;
		num_threads = 8;
		miners = new Thread[num_threads];
		tasks = new FutureTask[num_threads];

	}
	public MiningManager(Client client, int threads) {
		host = client;
		self = this;
		num_threads = threads;
		miners = new Thread[num_threads];
		tasks = new FutureTask[num_threads];
	}
	public void setBlock(Block b) {
		currentBlock = b;
		updateBlock();

	}

	public void updateBlock() {
		header = currentBlock.setBlockHeader();
	}

	public void foundBlock(int salt) {
		// send the salt in
		paused = true;
		// call back to host to add the found block, which is the block as the salt is set the found salt

	}

	@Override
	public void run() {

		// this should only return if we have found a block
		// otherwise,
		System.out.println("HI\n");
		running = true;
		paused = false;
		// if 
		int cnt = 0;
		long begin = System.currentTimeMillis();
		while (running && cnt < 500) {
			if (paused) {
				try {
					if (firstPause) {
						// clear threads on first pause
						for (int i=0;i<num_threads;i++) {
							tasks[i] = null;
							miners[i] = null;
						}
						firstPause = false;
					}
					//sleep for a bit before checking for state again
					Thread.sleep(1000);

				} catch (InterruptedException e) {

					Logging.log("Miners were not running, no nothing to pause");
					continue;
				}
			}
			else {
				// we do this so when it pauses, it will clear out the existing data

				firstPause = true;
				updateBlock();

				// init tasks if not done, num_threads is maximum number of tasks running at once
				for (int i=0;i<num_threads;i++) {
					if (tasks[i] != null) {

						if (tasks[i].isDone()) {
							try {
								HashReturn result = (HashReturn) tasks[i].get();
								if (result.success) 
									foundBlock(result.salt);

							} catch (Exception e) {
								e.printStackTrace();
								Logging.log("no idea what happened but something is very wrong");
								return;
							}

						} else {
							continue;
						}
					}
					// create new task
					tasks[i] = new FutureTask<HashReturn>(new MiningTask(header, i));
					miners[i] = new Thread(tasks[i]);
					miners[i].start();
				}

				// join them all
				for (int i=0;i<num_threads;i++) {
					if (tasks[i].isDone()) {
						try {
							// so a task doesn't stall the rest, will just request it later when it's available
							HashReturn result = (HashReturn) tasks[i].get();
							if (result.success)
								foundBlock(result.salt);
							tasks[i] = null;

							
						} catch (Exception e) {
							e.printStackTrace();
							Logging.log("no idea what happened but something is very wrong");
							return;
						}
					}
				}
				try {
					// can make ~5-10% faster by checking more frequently
					// is it worth i dont know if i can make that decision lol but whatever
					// it's not that big of a difference

					// essentially by checking 5x more often, 10% more tasks have finished
					// not really worth, but we are not checking that much in the first place so maybe it is a non-issue
					Thread.sleep(5);

				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}
		System.out.println("Took " + (System.currentTimeMillis() - begin));
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