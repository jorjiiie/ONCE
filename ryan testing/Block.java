import java.security.*;

// no idea what i'm doign so i'm replacing hashes and everything cipher-y with ints for now
// job of the block isn't to see if it's valid or not
// that is the job of the client/transaction being compiled into a block
public class Block extends CRYPTO implements java.io.Serializable
{

	private int height;
	private long seed;
	private int nxt;
	private long timeStamp;
	private int randNumber;
	private PublicKey miner_id;

	private Transaction[] transactions;
	private MerkleTree mTree;
	
	// no idea how to make unique ids LOL
	// how to uniquely hash?
	private byte[] blockHash,previous_block;

	public Block() {
		super(2);
		height = -1;
	}
	public Block(int h, Transaction[] t, byte[] p_block) {
		super(2);
		seed = System.currentTimeMillis();
		height = h;
		transactions = t;
		previous_block = p_block;
		mTree = new MerkleTree(t);
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
	private int quickRandom() {
		nxt = (int) (seed * nxt * nxt + 1);
		return nxt;
	}
	public boolean hash() {
		try {
			timeStamp = System.currentTimeMillis();
			// simulate rnadom by just doing timestamp^2 + 1
			randNumber = (int) (timeStamp * (int)(Math.random() * 420));

			String toHash = "" + timeStamp + randNumber  + HashUtils.byteToHex(mTree.getRootHash());
			blockHash = HashUtils.hash(toHash);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public String toString() {
		String ret = "";
		ret += "Height: " + height + "\n";
		return ret;

	}
	public boolean less(int n) {
		int current = 0;
		outer:
			for (int i=0;i<32;i++) {
				for (int j=0;j<8;j++) {
					if (((blockHash[i] >> j) & 1) != 0) {
						current = i*8 + j;
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
		Block b = new Block(1, t, null);
		for (int i=0;i<50000000;i++) {
			b.hash();
			if (b.less(23)) {
				System.out.println("num: " + i + " HASH: " + HashUtils.byteToHex(b.getHash()));
			}
		}
	}
}