package ONCE.core;

import java.math.BigInteger;
import java.util.Random;
import java.util.ArrayList;
import java.io.Serializable;
/*
 * Ryan Zhu
 * Block that has transactions and a hash that uses transaction + 
 *
 */


// performance improvements:
// instead of getting time every second, do every like 1000 hashes or smth
// do binary comparisons rather than binary -> string -> binary (java moment lol)
// the nonusage of string would probably make the hashing about 20x faster but it really doesnt matter lol

public class Block implements Serializable {
	
	// tHash is a local variable used for verification (transaction hash)
	public static final int MINING_DIFFICULTY = 0;
	public static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";
	public static final Block GENESIS_BLOCK = getGenesis();
	public static final long BLOCK_REWARD = 25;

	private String blockHeader;
	private String blockHash, tHash, previousHash;
	private byte[] byteHash;
	// private Transaction[] transactions;

	// no merkle !
	private ArrayList<Transaction> transactions;

	private long salt = 0;

	// lmao what is this please do not do this just have a long as timestamp...
	private long timestamp;
	private BigInteger miner;
	private int depth;
	private Random rand;

	public static Block getGenesis() {
		Block b = new Block(null, null, 0);
		b.previousHash = GENESIS_HASH;
		b.setTimestamp(System.currentTimeMillis());
		b.hash();
		return b;
	}
	/**
	 * Constructor for a block
	 * @param _transactions transactions array
	 * @param _miner public address for the miner of the block
	 */
	public Block(Transaction[] _transactions, BigInteger _miner, int depth) {

		transactions = new ArrayList<Transaction>();
		if (_transactions != null) {
			for (int i=0; i<_transactions.length; i++) {
				transactions.add(_transactions[i]);
			}
			hashTransactions();
		}
		miner = _miner;
		this.depth = depth;
		rand = new Random(System.nanoTime());
	}

	/**
	 * Constructs for a block, used when reading off disc or recieving a new block
	 * @param _transactions transactions array
	 * @param _miner public address for the miner of the block
	 * @param _blockHash _blockHash hash of the block
	 */
	public Block(Transaction[] _transactions, BigInteger _miner, String _blockHash, String _previousHash, long _salt, int _depth, long _timestamp) {

		transactions = new ArrayList<Transaction>();
		for (int i=0; i<_transactions.length; i++) {
			transactions.add(_transactions[i]);
		}
		miner = _miner;

		blockHash = _blockHash;
		previousHash = _previousHash;
		salt = _salt;
		depth = _depth;
		timestamp = _timestamp;
		rand = new Random(System.nanoTime());
	}

	/**
	 * Copy constructor
	 * @param block block to copy
	 * @param read true if reading off disc (complete), otherwise incomplete
	 */
	public Block(Block block, boolean read) {
		if (read) {
			byteHash = block.byteHash.clone();
			salt = block.salt;
			blockHash = block.blockHash;
			tHash = block.tHash;
			transactions = new ArrayList<Transaction>(block.transactions.size());
			for (int i=0; i<transactions.size(); i++) {
				transactions.set(i,block.transactions.get(i));
			}
		}
		timestamp = block.timestamp;
		previousHash = block.previousHash;
		depth = block.depth;
		


		rand = new Random(System.nanoTime());
	}

	public String toString() {
		if (blockHash==null)
			blockHash = HashUtils.byteToHex(byteHash);
		return "Hash: " + blockHash + "\nTransaction Hash: " + tHash + "\nPrevious Block: " + previousHash + "\nSalt: " + salt + "\nMiner: " + miner + "\nDepth: " + depth + "\nTimestamp: " + timestamp;
	}

	// for hash
	public String setBlockHeader() {
		blockHeader = ""+tHash+previousHash+ miner+ depth+timestamp;
		return blockHeader;
	}
	public String getBlockHeader() {
		return blockHeader;
	}

	public String getHeaderMiner() {
		return tHash+previousHash+ miner+depth;
	}

	/**
	 * Returns the full block header that is used for hashing
	 * Since the salt will not change between calls, it is assumed that the code that uses this will change the salt
	 * @return salt concatenated to the blockHeader
	 */
	public String str() {
		return tHash+previousHash+ miner+depth+timestamp+salt;
	}

	/**
	 * Hash the transactions into a linear thing LOL
	 */
	// if you transitioned into a merkletree then you don't need to send the whole tree during sending, only the header
	// and tx and the other person can build it themselves
	public void hashTransactions() {
		if (transactions.size() < 1)
			return;
		tHash = HashUtils.sHash(transactions.get(0).toString());
		for (Transaction tx : transactions)
			tHash = HashUtils.sHash(tHash + tx.toString());
	}

	/**
	 * Checks if transaction is already in + is valid and updates the transaction hash as well
	 * @param t transaction
	 */
	public boolean addTransaction(Transaction t) {
		// should this do this idfk but it should work sooo
		if (t==null || t.verify() == false || transactions.contains(t))
			return false;

		transactions.add(t);

		tHash = HashUtils.sHash(tHash + t.toString());

		return true;
	}
	/**
	 * Verify that the block is valid (Not the data integrity but rather the hash)
	 * @return boolean determining validity of the block
	 */
	public boolean verify() {

		for (Transaction tx : transactions) {
			if (tx.verify() == false)
				return false;
		}

		hashTransactions();
		blockHash = HashUtils.byteToHex(byteHash);
		// System.out.println("hi");
		// System.out.println(toString());
		// System.out.println(HashUtils.sHash(str()));
		return (blockHash.equals(HashUtils.sHash(str())));
	}

	/**
	 * Not necessary and likely useless
	 * @return hash of the block in hex
	 */
	public String hashString() {	
		salt = rand.nextLong();
		return blockHash= HashUtils.sHash(str());
	}
	/**
	 * Hashes the block
	 * Sets the byteHash to the sha-256 hash
	 */
	public void hash() {
		byteHash = HashUtils.hash(str());
	}

	/**
	 * Compares the hash of the block to a string
	 * @param cmp string to compare to
	 * @return true if less than, false otherwise
	 */
	public boolean lessThan(String cmp) {
		// this is likely useless
		String bHash = HashUtils.hexToByte(blockHash);
		int minLength = Math.min(cmp.length(), bHash.length());
		for (int i=0;i<minLength;i++) {
			if (cmp.charAt(i) != bHash.charAt(i)) {
				if (cmp.charAt(i) == '1')
					return true;
				return false;
			}
		}
		return false;
	}

	// should rename leadingzeroes
	/**
	 * Primary method for checking if less than
	 * @param n minimum number of leading zeroes
	 * @return true if number of leading zeroes >= n
	 */
	public boolean lessThan(int n) {
		// compare this hash to (0 * n) 1
		// if less than, returns true
		int current = 256;
		outer:
			for (int i=0;i<32;i++) {
				for (int j=7;j>=0;j--) {
					if (((byteHash[i] & (1 << j)) != 0)) {
						current = i*8 + (7-j);
						break outer;
					}
				}
			}
		return current < n;
	}

	/**
	 * Same as {@link lessThan(int)} but static
	 * @param hash sha-256 hash of the block
	 * @param n minimum number of leading zeroes
	 * @return true if number of leading zeroes >= n
	 */
	public static boolean lessThan(byte[] hash, int n) {
		int current = 256;
		outer:
			for (int i=0;i<32;i++) {
				for (int j=7;j>=0;j--) {
					if (((hash[i] & (1 << j)) != 0)) {
						current = i*8 + (7-j);
						break outer;
					}
				}
			}
		return current < n;
	}

	/**
	 * returns the hash of the block
	 * @return hash of the block
	 */
	public String getBlockHash() {
		return blockHash;
	}

	public void setHash(byte[] hash) {
		byteHash = hash.clone();
		blockHash = HashUtils.byteToHex(byteHash);
	}
	public void setHash(String hash) {
		this.blockHash = hash;
	}

	public String getPrevious() {
		return previousHash;
	}

	public void setPrevious(String prev) {
		previousHash = prev;
	}
	public void setSalt(long _salt) {
		salt = _salt;
	}

	public int getDepth() {
		return depth;
	}
	public void setTimestamp(long ts) {
		timestamp = ts;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public Transaction[] getTransactions() {
		Transaction[] ret = new Transaction[transactions.size()];
		for (int i=0;i<transactions.size();i++) {
			ret[i] = transactions.get(i);
		}
		return ret;
	}
	public void newSalt() {
		salt = rand.nextLong();
	}
	public BigInteger getMiner() {
		return miner;
	}
	public void prep() {
		// the fact that i need this
		blockHash = HashUtils.byteToHex(byteHash);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof Block) {
			hash();
			((Block) other).hash();
			return byteHash.equals(((Block) other).byteHash);
		}
		return false;
	}


	/**
	 * Gets hash code of the block, which is just the hashcode of the hash from BigInteger
	 * @return hash code
	 */
	public static void main(String[] args) {
		long st = System.currentTimeMillis();
		RSA carl = new RSA();
		RSA joe = new RSA();
		System.out.println(System.currentTimeMillis() - st + " ms for RSA initialization");
		Transaction[] ts = new Transaction[10];
		for (int i=0;i<10;i++) {
			ts[i] = new Transaction(carl.getPublic(), joe.getPublic(), 5);
			ts[i].sign(joe);
			System.out.println(ts[i].verify());
		}

		Block nb = new Block(ts, carl.getPublic(),1);
		nb.hashTransactions();
		long start = System.currentTimeMillis();
		long cnt = 0;
		do {
			nb.newSalt();
			nb.hash();
			cnt++;
			//System.out.println("hello");
		} while (nb.lessThan(25));
		long end = System.currentTimeMillis();
		System.out.println(cnt + " hashes in " + (end-start) + " ms or " + cnt*1000.0/(end-start) + "h/s");
		System.out.println(nb);
		System.out.println(nb.verify());
		System.out.println(HashUtils.hexToByte(nb.getBlockHash()));
		System.out.println(nb.lessThan("0001"));
		System.out.println(nb.hashCode());
	}
}
