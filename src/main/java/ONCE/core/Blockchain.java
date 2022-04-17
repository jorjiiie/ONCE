package ONCE.core;

import java.util.HashMap;
import java.util.HashSet;
import java.math.BigInteger;
public class Blockchain {

	// need to turn in to a db but for now, do this
	// get a lock 
	private HashMap<String, BlockRecord> blockchain;

	// also need a db
	// also needs a lock
	private HashMap<String, Transaction> mainTxChain;
	private HashMap<String, Transaction> workingTxChain;
	// technically have a max balance but this is implementation agnostic (god i love the word agnostic dont I)
	// change this to string (hash of bigint)
	private HashMap<BigInteger, Long> balances;

	// balances of block we are currently workign on (no overspending during a block)
	private HashMap<BigInteger, Long> workingBalances;

	// when a new highest block is found, go back until both blocks are at a common ancestor
	// highest block
	private Block highestBlock = null;
	private int currentDepth = -1;
	public Blockchain() {
		// new blockchain wub a dub dub
		blockchain = new HashMap<String, BlockRecord>();

		mainTxChain = new HashMap<String, Transaction>();

		balances = new HashMap<BigInteger, Long>();

		workingBalances = new HashMap<BigInteger, Long>();
		workingTxChain = new HashMap<String, Transaction>();
		highestBlock = null;
	}

	public Blockchain(Blockchain toCopy) {
		blockchain = new HashMap<String, BlockRecord>(toCopy.blockchain);
		mainTxChain = new HashMap<String, Transaction>(toCopy.mainTxChain);
		balances = new HashMap<BigInteger, Long>(toCopy.balances);
		workingBalances = new HashMap<BigInteger, Long>(toCopy.workingBalances);
		workingTxChain = new HashMap<String, Transaction>(toCopy.workingTxChain);
		highestBlock = toCopy.highestBlock;
	}

	// add a block to the blockchain, definitely good to go though (must be checked)
	private void addToBlockchain(Block b) {
		BlockRecord newRec = new BlockRecord(b,0);
		blockchain.put(b.getBlockHash(), newRec);
		addTransactions(b);
	}
	/**
	 * Adds a block to the blockchain
	 * Unless a block is invalid (bad transactions, fake hash, etc), it will always add the block regardless of depth
	 * Will also adjust the current transactions (balances) to the current highest block (but this is implementation agnostic)
	 * if you do this off a disc read you will get fucked as it assumes a complete blockchain (no floating blocks or smth)
	 * @param b Block to be added
	 * @return highest level block
	 */
	public boolean addBlock(Block b) {
		b.prep();
		String prevBlock = b.getPrevious();

		// check for genesis & make sure it is the right one
		if (prevBlock.equals(Block.GENESIS_HASH)) {
			// only make one of these :sunglasses:
			// check the message/other stuff
			// do nothing else
			addToBlockchain(b);
			highestBlock = b;
			// Logging.log("GENSIS BLOCK ADDED, highest block is " + highestBlock);
			return true;
		} 

		BlockRecord rec = blockchain.get(prevBlock);

		// nothing was found
		if (rec == null) {
			Logging.log("Block rejected for having invalid previous block");
			return false;
		}

		// timestamp has to be afterwards
		if (b.getTimestamp() <= rec.block.getTimestamp()) {
			Logging.log("Block rejected for having a bad timestamp (before previous block");
			return false;
		}

		// validate block
		if (verifyTransactions(b) == false) {
			// THIS HAS TO CHECK FOR MONEYS TOOO
			// this (will somehow) check for the hashes too so the same one can't be used twice,
			// new id generated on each one 
			Logging.log("Block rejected for being invalid (transactions or hash)");
			return false;
		}

		if (b.lessThan(Block.MINING_DIFFICULTY) == true) {
			Logging.log("Block rejected for being less than acceptable diffculty");
			return false;
		}


		// update transaction count
		incConfirmations(b.getBlockHash());

		// if this is the new highest block, we have some issues to go through
		// add another if statement for if previous hash == highestblock
		if (highestBlock.getBlockHash().equals(b.getPrevious())) {
			if (highestBlock.getDepth() != b.getDepth() - 1) {
				Logging.log("Rejected for having bad depth");
				return false;
			}
			highestBlock = b;


		}
		else if (highestBlock.getDepth() < b.getDepth()) {

			Logging.log("Switching branches of blockchain");
			// alternatively we can just go backwards and redo the entire blockchain...
			// copy hashmap so we don't do something sketchy
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
						return false;
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
							return false;
						}
					}
					Block tempBlock = queryBlock(currentHash_New);
					if (tempBlock == null) {
						Logging.log("Something is very wrong in blockchain, kind of incomplete or data is bad");
						return false;
					}
					currentHash_New = tempBlock.getPrevious();
				}

				// double step each time to hit that common ancestor :sunglasses
				step *= 2;
			}

			Logging.log("Junction: " + commonAncestor);
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
		} else {

		}


		addToBlockchain(b);
		balances.compute(b.getMiner(), (k,v) -> (v==null)?Block.BLOCK_REWARD:v+Block.BLOCK_REWARD);

		return true;
	}

	public boolean testAdd(Block b) {
		Blockchain tmp = new Blockchain(this);
		return (tmp.addBlock(b));
	}
	public void incConfirmations(String hash) {
		BlockRecord rec =  blockchain.get(hash);
		while (rec != null) {
			// inc the ahahh
			rec.confirmations++;
			rec = blockchain.get(rec.block.getPrevious());
		}
	}

	public Block getHighestBlock() {
		return highestBlock;
	}

	/**
	 * Verifies transactions for validity & balance
	 * @param b block with transactions
	 * @return true if valid, false if not
	 */
	public boolean verifyTransactions(Block b) {
		Transaction[] txArray = b.getTransactions();

		HashMap<BigInteger, Long> tempBalance = new HashMap<BigInteger, Long>();
		for (Transaction tx : txArray) {
			// if already contains or invalid
			if (mainTxChain.containsKey(tx.getHash()) || !tx.verify()) {
				return false;
			}
			// honestly might be easier to have input/output transactions
			// but this way it is UNTRACEABLE LOLLL
			// but yeah input/output transactions would be hella easy

			tempBalance.compute(tx.getReciever(), (k,v) -> (v==null)?tx.getAmount():v+tx.getAmount());
			tempBalance.compute(tx.getSender(), (k,v) -> (v==null)?-tx.getAmount():v-tx.getAmount());


			Logging.log("ADDED A TRANSACTION SOMEHOW");
			tempBalance.forEach((k,v) -> Logging.logBalance(k,v));
			Logging.log("Balance of sender: " + balances.getOrDefault(tx.getSender(),0L) + " " + tempBalance.get(tx.getSender()));
			if (balances.getOrDefault(tx.getSender(),0L) + tempBalance.get(tx.getSender()) < 0) {
				// broke ass bitch
				Logging.log("SOMEBODY IS TOO POOR: " + tx.getSender() + " trying to send " + tx.getAmount() + " to : " + tx.getReciever());
				return false;
			}
		}
		return true;
	}

	/**
	 * Verifies a singular transaction for validity & balance
	 * @param tx transaction
	 * @return true if valid, false if not
	 */
	public boolean verifyTransaction(Transaction tx) {
		if (mainTxChain.containsKey(tx.getHash()) || !tx.verify()) {
			Logging.log("Transaction already in blockchain or transaction failed verification");
			return false;
		}

		if (balances.getOrDefault(tx.getSender(),0L) - tx.getAmount() < 0) {
			Logging.log("broke ass bitch");
			return false;
		}

		return true;
	}

	/**
	 * Verifies a singular transaction for validity & balance, and adds it to a temporary list of balances to be incorporated in the next block
	 * @param tx transaction
	 * @return true if valid, false if not
	 */
	public boolean verifyWorkingTransaction(Transaction tx) {
		if (workingTxChain.containsKey(tx.getHash()) || !tx.verify()) {
			Logging.log("Transaction already in blockchain or transaction failed verification", "WORKING TRANSACTIONS");
			return false;
		}

		if (balances.getOrDefault(tx.getSender(),0L) + workingBalances.getOrDefault(tx.getSender(), 0L) - tx.getAmount() < 0) {
			Logging.log("broke ass bitch", "WORKING TRANSACTIONS");
			return false;
		}

		return true;
	}

	/**
	 * Adds a transaction to the working transactions
	 * @param tx transaction 
	 */
	public void addWorkingTransaction(Transaction tx) {
		workingBalances.compute(tx.getSender(), (k,v) -> (v==null) ? -tx.getAmount() : v - tx.getAmount());
		workingBalances.compute(tx.getReciever(), (k,v) -> (v==null) ? tx.getAmount() : v + tx.getAmount());
		workingTxChain.put(tx.getHash(), tx);
	}

	/**
	 * Clears out working balances (should be called every time the working block is reset)
	 */
	public void resetWorkingTransactions() {
		workingBalances.clear();
		workingTxChain.clear();
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

			balances.compute(tx.getReciever(), (k,v) -> (v==null)?tx.getAmount():v+tx.getAmount());
			balances.compute(tx.getSender(), (k,v) -> v-tx.getAmount());

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
			// add back balance...
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

	public void printBalances() {
		Logging.log("printing balances :(");
		balances.forEach((k,v) -> Logging.logBalance(k,v));
	}

	public Long queryBalance(BigInteger address) {
		return balances.getOrDefault(address, 0L);
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
