package ONCE.core;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Random;
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
	public static final int MINING_DIFFICULTY = 25;
	private String blockHash, tHash, previousHash;
	private byte[] byteHash;
	private Transaction[] transactions;
	private long salt;

	// lmao what is this please do not do this just have a long as timestamp...
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	private String timeString;
	private BigInteger miner;
	private int depth;
	private Random rand;
		
	/**
	 * Constructor for a block
	 * @param _transactions transactions array
	 * @param _miner public address for the miner of the block
	 */
	public Block(Transaction[] _transactions, BigInteger _miner) {
		transactions = _transactions;
		miner = _miner;
		rand = new Random(System.nanoTime());
	}

	/**
	 * Constructs for a block, used when reading off disc or recieving a new block
	 * @param _transactions transactions array
	 * @param _miner public address for the miner of the block
	 * @param _blockHash _blockHash hash of the block
	 */
	public Block(Transaction[] _transactions, BigInteger _miner, String _blockHash, String _previousHash, long _salt, int _depth, Timestamp _timestamp) {
		transactions = _transactions;
		miner = _miner;
		blockHash = _blockHash;
		previousHash = _previousHash;
		salt = _salt;
		depth = _depth;
		timestamp = _timestamp;
		rand = new Random(System.nanoTime());
	}

	public String toString() {
		blockHash = HashUtils.byteToHex(byteHash);
		return blockHash + " " + tHash + " " + previousHash + " " + salt + " " + miner + " " + depth + " " + timestamp;
	}

	// for hash
	public String str() {
		return tHash + " " + previousHash + " " + salt + " " + miner + " " + depth + " " + timeString;
	}
	public void hashTransactions() {
		if (transactions.length < 1)
			return;
		tHash = HashUtils.sHash(transactions[0].toString());
		for (int i=1;i<transactions.length;i++) {
			tHash = HashUtils.sHash(tHash + transactions[i].toString());
		}
	}
	public boolean verify() {
		// verify that everything is cash money
		// if any transactions are faulty
		for (int i=0;i<transactions.length;i++) {
			if (transactions[i].verify() == false) {
				return false;
			}
		}
		hashTransactions();
		blockHash = HashUtils.byteToHex(byteHash);
		// System.out.println("hi");
		// System.out.println(toString());
		// System.out.println(HashUtils.sHash(str()));
		return (blockHash.equals(HashUtils.sHash(str())));
	}
	public void setTime() {
		timestamp.setTime(System.currentTimeMillis());
		timeString = timestamp.toString();
	}
	public String hashString() {	
		salt = rand.nextLong();
		return blockHash= HashUtils.sHash(str());
	}
	public void hash() {
		salt = rand.nextLong();
		byteHash = HashUtils.hash(str());
	}
	public boolean lessThan(String cmp) {
		String bHash = HashUtils.hexToByte(blockHash);
		int minLength = Math.min(cmp.length(), bHash.length());
		for (int i=0;i<minLength;i++) {
			if (cmp.charAt(i) != bHash.charAt(i)) {
				if (cmp.charAt(i) == '1')
					return false;
				return true;
			}
		}
		return false;
	}
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
	public String getBlockHash() {
		return blockHash;
	}

	public String getPrevious() {
		return previousHash;
	}

	/**
	 * Gets hash code of the block, which is just the hashcode of the hash from BigInteger
	 * @return hash code
	 */
	// means about ~44000 blocks before collisions
	// this is definitely NOT language agnostic but it's not necessary bc each language can handle
	// the hashsets/database differently
	@Override 
	public int hashCode() {

		System.out.println(byteHash.hashCode() +" " + HashUtils.byteToHex(byteHash));
		return (new BigInteger(byteHash)).hashCode();
	}
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

		Block nb = new Block(ts, carl.getPublic());
		nb.hashTransactions();
		long start = System.currentTimeMillis();
		long cnt = 0;
		do {
			nb.hash();
			cnt++;
			//System.out.println("hello");
		} while (nb.lessThan(16));
		long end = System.currentTimeMillis();
		System.out.println(cnt + " hashes in " + (end-start) + " ms or " + cnt*1000.0/(end-start) + "h/s");
		System.out.println(nb);
		System.out.println(nb.verify());
		System.out.println(HashUtils.hexToByte(nb.getBlockHash()));
		System.out.println(nb.lessThan("0001"));
		System.out.println(nb.hashCode());
	}
}
