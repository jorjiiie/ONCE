package ONCE.core;

import java.util.HashMap;
import java.util.HashSet;
public class Blockchain {

	// need to turn in to a db but for now, do this
	// get a lock 
	private HashMap<String, BlockRecord> blockchain;

	// also need a db
	// also needs a lock
	private HashMap<String, Transaction> mainTxChain;
	// when a new highest block is found, go back until both blocks are at a common ancestor
	// highest block
	private Block highestBlock = null;
	private int currentDepth = -1;
	public Blockchain() {
		// new blockchain wub a dub dub
		blockchain = new HashMap<String, BlockRecord>();
		highestBlock = null;
	}

	/**
	 * Adds a block to the blockchain
	 * Unless a block is invalid (bad transactions, fake hash, etc), it will always add the block regardless of depth
	 * Will also adjust the current transactions (balances) to the current highest block (but this is implementation agnostic)
	 * if you do this off a disc read you will get fucked as it assumes a complete blockchain (no floating blocks or smth)
	 * @param b Block to be added
	 * @return highest level block
	 */
	public Block addBlock(Block b) {
		String prevBlock = b.getPrevious();
		
		// check for genesis & make sure it is the right one
		if (prevBlock == Block.GENESIS_HASH) {
			// only make one of these :sunglasses:
			// check the message/other stuff
			// do nothing else
			highestBlock = b;
			return highestBlock;
		} 

		BlockRecord rec = blockchain.get(prevBlock);

		// nothing was found
		if (rec == null) {
			Logging.log("Block rejected for having invalid previous block");
			return highestBlock;
		}

		// timestamp has to be afterwards
		if (b.getTimestamp() <= rec.block.getTimestamp()) {
			Logging.log("Block rejected for having a bad timestamp (before previous block");
			return highestBlock;
		}

		// validate block
		if (b.verify() == false) {
			// THIS HAS TO CHECK FOR MONEYS TOOO
			// this (will somehow) check for the hashes too so the same one can't be used twice,
			// new id generated on each one 
			Logging.log("Block rejected for being invalid (transactions or hash");
			return highestBlock;
		}

		if (b.lessThan(Block.MINING_DIFFICULTY) == false) {
			Logging.log("Block rejected for being less than acceptable diffculty");
			return highestBlock;
		}

		BlockRecord newRec = new BlockRecord(b, 0);
		Logging.log("Block accepted into blockchain");
		blockchain.put(b.getBlockHash(), newRec);

		// update transaction count
		incConfirmations(b.getBlockHash());

		// if this is the new highest block, we have some issues to go through
		if (highestBlock.getDepth() < b.getDepth()) {
			// now we have to go backwards & figure out the transactions
			HashSet<String> seenHashes = new HashSet<String>();

			String currentHash_Old = highestBlock.getBlockHash();
			String currentHash_New = b.getBlockHash();

			String commonAncestor = Block.GENESIS_HASH;

			int step = 1;
			// we can do this bc old is 100% at least on the same level as it

			outer: while (!currentHash_Old.equals(Block.GENESIS_HASH)) {
				for (int i = 0; i < step && !currentHash_Old.equals(Block.GENESIS_HASH); i++) {
					seenHashes.add(currentHash_Old);
					Block tempBlock = queryBlock(currentHash_Old);
					if (tempBlock == null) {
						Logging.log("Something is very wrong in blockchain, kind of incomplete or data is bad");						// force it to break out???
						break;
					}
					currentHash_Old = tempBlock.getPrevious();
				}

				for (int i = 0; i < step  && !currentHash_New.equals(Block.GENESIS_HASH); i++) {
					if (seenHashes.contains(currentHash_New)) {
						// this is the most recent common ancestor, remove until here??
						commonAncestor = currentHash_New;
						break outer;
					} else {
						// remove this block from record
						boolean res = removeTransactions(queryBlock(currentHash_New));
						if (res == false) {
							Logging.log("Something went wrong removing ancestors when resolving new highest block, probably about to crash");
						}
					}
					Block tempBlock = queryBlock(currentHash_New);
					if (tempBlock == null) {
						Logging.log("Something is very wrong in blockchain, kind of incomplete or data is bad");
						break outer;
					}
					currentHash_New = tempBlock.getPrevious();
				}

				// double step each time to hit that common ancestor :sunglasses
				step *= 2;
			}

			// add transactions until we are at the new top level block (go from top level -> common ancestor)
			// we can do this blindly because it's just a greedy algorithm, we assume that the smaller subproblems are ok
			// but when we are adding a new block from a different branch, we have to make a copy of the thing first to test balances before accepting it into the main 
			String currentHash = b.getBlockHash();
			while (!currentHash.equals(commonAncestor)) {
				boolean res = addTransactions(queryBlock(currentHash));
				if (res == false) {
					Logging.log("Something went wrong when trying to resolve new highest block during adding transactions");
				}
				// no need to check bc we already did lol
				currentHash = queryBlock(currentHash).getPrevious();
			}

			highestBlock = b;
		}

		Logging.log("Added block, highest block is " + highestBlock);
		return highestBlock;
	}
	public void incConfirmations(String hash) {
		BlockRecord rec =  blockchain.get(hash);
		while (rec != null) {
			// inc the ahahh
			rec.confirmations++;
			rec = blockchain.get(rec.block.getPrevious());
		}
	}

	/**
	 * Adds transactions of Block b into the hashmap
	 * @param b block with transactions
	 * @return true if transactions were successfully added, false if there is conflit (and block should be discarded)
	 */
	public boolean addTransactions(Block b) {
		Transaction[] txArray = b.getTransactions();

		for (Transaction tx : txArray) {
			if (mainTxChain.containsKey(tx.getHash())) 
				return false;
		}
		for (Transaction tx : txArray) {
			mainTxChain.put(tx.getHash(), tx);
		}
		return true;
	}

	/**
	 * Removes transactions of Block b from the hashmap (db)
	 * @param b block with transactions
	 * @return true if all transactions were successfully removed
	 */
	public boolean removeTransactions(Block b) {
		Transaction[] txArray = b.getTransactions();

		for (Transaction tx : txArray) {
			if (mainTxChain.containsKey(tx.getHash())) 
				return false;
		}
		for (Transaction tx : txArray) {
			mainTxChain.remove(tx.getHash());
		}
		return true;
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
		int confirmations;
		// hash of next blocks?!?!?!?!?!??!?!?!? don't really need to plus should be easy to impl
		public BlockRecord(Block _block, int _confirmations) {
			block = _block;
			confirmations = _confirmations;
		}
	
	}
}
