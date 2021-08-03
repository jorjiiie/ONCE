import java.security.*;
import java.math.BigInteger;
import java.math.BigDecimal;

// no idea what i'm doign so i'm replacing hashes and everything cipher-y with ints for now
// job of the block isn't to see if it's valid or not
// that is the job of the client/transaction being compiled into a block
public class Block extends CRYPTO implements java.io.Serializable
{
	private static double versionNumber = 1.0;
	
	private String bits; // I dont wanna deal with datatypes
						 // when we test make the starting bits "00000000"
	// all of the dynamic target stuff should be implemented at a later date. for now it'll be easier to hard code 
	private int height;
	
	private long timeStamp;
	private int randNumber;
	private PublicKey miner_id;

	private Transaction[] transactions;

	private MerkleTree mTree;
	private byte[] mRoot;	
	private String mRootHex;
	// no idea how to make unique ids LOL
	// how to uniquely hash?
	private byte[] blockHash,previous_block;
	private String previousHash;
	public Block() {
		super(2);
		height = -1;
	}
	public Block(int h, Transaction[] t, byte[] p_block, String bits) {
		super(2);
		height = h;
		this.bits = bits;
		transactions = t;
		previous_block = p_block;
		mTree = new MerkleTree(t);
		mRoot = mTree.getRootHash();
		mRootHex = HashUtils.byteToHex(mRoot);
	}

	public void set_height(int h) {
		height = h;
	}
	public int get_height() {
		return height;
	}
	public void set_transactions(Transaction[] t) {
		transactions=t;
	}
	public Transaction[] get_transactions() {
		return transactions;
	}
	public void set_previous(byte[] p_block) {
		previous_block=p_block;
	}
	public byte[] getHash() {
		return blockHash;
	}
	public byte[] get_previous() {
		return previous_block;
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
	public int getTarget() // targets should be 48 characters / 24 bytes. First 3 bytes are replaced by the coefficient of the bits
	// make into string getter later
	{
		return 24;
//		int numTimes = Integer.parseInt(bits.substring(0, 2), 16) - 3; // accounts for first 3 bytes
//		String concatPre = "";
//		
//		for (int i = 0; i < 42 - numTimes * 2; i++) // the first 2 characters denote the number of "bytes" that are 0
//		{
//			concatPre += '0';
//		}
//		
//		String concatSuf = "";
//		for (int i = 0; i < numTimes * 2; i++)
//		{
//			concatSuf += '0';
//		}
//		return concatPre + bits.substring(2, 8) + concatSuf;
//		//return bits % 0x1000000L * ((long)(Math.pow(0x10, bits / 0x100000000L * 16 + bits / 0x10000000L)));
	}

	public boolean hash() {
		try {
			timeStamp = System.currentTimeMillis() / 1000L; // is epoch time
			// simulate random by just doing 
			randNumber = (int) (Math.random() * 420); 
			// we just do random instead of using nonces

			blockHash = HashUtils.hash("" + versionNumber + previousHash + mRootHex + timeStamp + getTarget() + randNumber);
//			if(height % 2016 == 0)
//			{
//				int prefix = Integer.parseInt(bits.substring(0,2), 16);
//				double percentAvg = calculateAverageTime(0, 0) / 2016.0;
//				BigInteger beeg1 = new BigInteger(bits.substring(2,8), 16);
//				BigDecimal beeg2 = new BigDecimal(beeg1);
//				beeg2 = beeg2.multiply(new BigDecimal(percentAvg));
//				beeg1 = beeg2.toBigInteger();
//				bits = beeg1.toString(16);
//				if (bits.length() != 6) // assuming valid hashes are larger than target
//				{
//					int newzeroes =  bits.length() - 6;
//					prefix += newzeroes;
//				}
//			}
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	private double calculateAverageTime(double sum, int heightreached) // properly implement once we get blockchains working
	{
		return -1.0;
//		if(heightreached < 2016)
//		{
//			heightreached++;
//			long timeDifference = timeStamp - previous_block.getTimeStamp();
//			double percentDifference = timeDifference / 20160D;
//			sum += percentDifference;
//			return previous_block.calculateAverageTime(sum, heightreached);
//		} else
//		{
//			return sum;
//		}
	}
	
	public String toString() {
		String ret = "";
		ret += "Height: " + height + "\n";
		return ret;

	}
	public boolean less(int n) { // still need to do code for when we do dynamic targets, once we get there
		int current = 256;
		outer:
			for (int i=0;i<32;i++) {
				for (int j=7;j>=0;j--) {
					if (((blockHash[i] & (1 << j)) != 0)) {
						current = i*8 + (7-j);
						break outer;
					}
				}
			}
		return current > n;
	}
	public static void main(String[] args) {
		User carl = new User();
		carl.initUser();
		User joe = new User();	
		joe.initUser();

		int n = 5;
		Transaction t[] = new Transaction[n];
		for (int i=0;i<n;i++) {
			t[i] = carl.sendTo(joe.getPublicKey(),5);
			// System.out.println(t[i]);
		}


		// uwu mining
		long HASHES = 50000000L;
		int found = 0;
		long start = System.currentTimeMillis();
		Block b = new Block(1, t, null, "18ffffff");
		for (long i=0;i<HASHES;i++) {
			b.hash();
			if (b.less(24)) {
				System.out.println("num: " + i + " HASH: " + HashUtils.byteToHex(b.getHash()));
				found++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(HASHES + " hashes took " + (end-start) + " ms, or about " + HASHES*1000.0/(end-start) + " hashes per second");
		System.out.println((end-start)/1000.0/found +" seconds per hash (" + found + ")");

	}
}