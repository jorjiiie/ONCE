import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Random;
/*
 * Ryan Zhu
 * Block that has transactions and a hash that uses transaction + 
 *
 */



// performance improvements:
// instead of getting time every second, do every like 1000 hashes or smth
// do binary comparisons rather than binary -> string -> binary (java moment lol)
// the nonusage of string would probably make the hashing about 20x faster but it really doesnt matter lol
public class Block {
	
	// tHash is a local variable used for verification (transaction hash)
	private String blockHash, tHash, previousHash;
	private Transaction[] transactions;
	private long salt;
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	private String timeString;
	private BigInteger miner;
	private int depth;
	private Random rand;
		
	public Block(Transaction[] _transactions, BigInteger _miner) {
		transactions = _transactions;
		miner = _miner;
		rand = new Random(System.nanoTime());
	}
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
		System.out.println("hi");
		System.out.println(toString());
		System.out.println(HashUtils.sHash(str()));
		return (blockHash.equals(HashUtils.sHash(str())));
	}
	public void setTime() {
		timestamp.setTime(System.currentTimeMillis());
		timeString = timestamp.toString();
	}
	public String hash() {	
		salt = rand.nextLong();
		return blockHash= HashUtils.sHash(str());
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
	public String getBlockHash() {
		return blockHash;
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
		nb.hashTransactions();
		long start = System.currentTimeMillis();
		long cnt = 0;
		do {
			nb.hash();
			cnt++;
			//System.out.println("hello");
		} while (nb.lessThan("0000000000000000001"));
		long end = System.currentTimeMillis();
		System.out.println(cnt + " hashes in " + (end-start) + " ms or " + cnt*1000.0/(end-start) + "h/s");
		System.out.println(nb);
		System.out.println(nb.verify());
		System.out.println(HashUtils.hexToByte(nb.getBlockHash()));
		System.out.println(nb.lessThan("0001"));
	}
}
