package ONCE.core;


import java.util.concurrent.atomic.AtomicInteger;

import java.util.HashMap;
public class Blockchain {

	// need to turn in to a db but for now, do this
	// get a lock 
	private HashMap<String, BlockRecord> blockchain;

	// also need a db
	// also needs a lock
	private HashMap<String, Transaction> mainTxChain;
	// highest block
	Block highestBlock;
	public Blockchain() {
		// new blockchain wub a dub dub
		blockchain = new HashMap<String, BlockRecord>();
		highestBlock = null;
	}

	/**
	 * Adds a block to the blockchain
	 * @return highest level block
	 */
	public Block addBlock(Block b) {
		String prevBlock = b.getPrevious();
		
		// if empty then we just add lol
		if (blockchain.isEmpty()) {
			BlockRecord rec = new BlockRecord(b, new AtomicInteger(1));
			Logging.log("Added new Block to blockchain");
			blockchain.put(b.getBlockHash(), rec);		
			highestBlock = b;
			return b;
		}

		/*
		if (blockchain.isEmpty() == false) {
			if (prevBlock == null) {
					return highestBlock;
			}
			BlockRecord rec = blockchain.get(prevBlock);
			if (rec == null) {
				return highestBlock;
			}
			// check for timestamp, has to be after
			if (b.getTimestamp() <= rec.block.getTimestamp()) {
				return highestBlock;
			}
		}
		*/



		// validate block
		if (b.verify() == false) {
			return highestBlock;
		}
		// needs the less
		if (b.lessThan(Block.MINING_DIFFICULTY) == false) {
			return highestBlock;
		}
		BlockRecord rec = new BlockRecord(b, new AtomicInteger(0));
		Logging.log("Added new Block to blockchain");
		blockchain.put(b.getBlockHash(), rec);

		// which block is on top
		new Thread() {
			public void run() {
				incConfirmations(b.getBlockHash());
			}
		}.start();
		if (highestBlock.getDepth() >= b.getDepth()) {
			return highestBlock;
		}

		// new highest block, replace the previous tx's with the ones in the new chain if it's a different chain

		highestBlock = b;
		// might want a method in logging that returns a readable block lol
		Logging.log("New highest block" + b);
		return highestBlock;
	}
	public void incConfirmations(String hash) {
		BlockRecord rec =  blockchain.get(hash);
		while (rec != null) {
			// inc the ahahh
			rec.confirmations.getAndIncrement();
			rec = blockchain.get(rec.block.getPrevious());
		}
	}

	public void printBlocks() {
		Logging.log("PRINTING");
		for (BlockRecord rec : blockchain.values()) {
			Logging.log(rec.block.toString());
		}
	}
	public Block queryBlock(String hash) {
		BlockRecord rec = blockchain.get(hash);
		if (rec == null)
			return null;
		return rec.block;
	}
	// class for handling block records
	// this makes adding blocks O(n) which is not the best...
	// can we get a lazy 
	// or an inverted fenwick?
	// its like they all go down to the bottom anyways, so we can have "split" nodes?
	// idk what im doing lol i should just think about this later and design some cool algo to do it later
	private class BlockRecord {
		Block block;
		AtomicInteger confirmations;
		// hash of next blocks?!?!?!?!?!??!?!?!? don't really need to plus should be easy to impl
		public BlockRecord(Block _block, AtomicInteger _confirmations) {
			block = _block;
			confirmations = _confirmations;
		}
	
	}
}
