
// no idea what i'm doign so i'm replacing hashes and everything cipher-y with ints for now
// job of the block isn't to see if it's valid or not
// that is the job of the client/transaction being compiled into a block
public class Block
{

	private int height;
	private int miner_id;

	private Transaction[] transactions;
	private MerkleTree m;
	
	// no idea how to make unique ids LOL
	// how to uniquely hash?
	private byte[] block_hash,previous_block;

	public Block()
	{
		height = -1;
		id = -1;
	}
	public Block(int h, Transaction[] t, int[] p_block)
	{
		height = h;
		transactions = t;
		previous_block = p_block;
	}

	public void set_height(int h)
	{
		height = h;
	}
	public int get_height()
	{
		return height;
	}
	public void set_transactions(Transaction[] t)
	{
		transactions=t;
	}
	public Transaction[] get_transactions()
	{
		return transactions;
	}
	public void set_previous(int[] p_block)
	{
		previous_block=p_block;
	}
	public int get_previous()
	{
		return previous_block;
	}
	public boolean set_hash()
	{
		try {
			hash();
			return true;
		}
		return false;

	}

	public boolean hash()
	{
		
	}
}