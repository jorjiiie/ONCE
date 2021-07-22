

public class Transaction 
{
	public static final int CONFIRMATION_THRESHOLD = 1;
	private int sender,reciever;
	private int send_count; // how many times the sender has made a transaction lol this is so bad
	private long amount;

	// somehow implement signature too idk how to do this yet
	// will auto reject if its not confirmed but it wont check outside of that
	// clients job^


	private int confirmations;
	private byte[] hash;
	private byte[] sig;


	public Transaction()
	{
		// joe
		sender = 0;
	}
	public Transaction(int s, int r, int amt, int s_cnt)
	{
		sender = s;
		reciever = r;
		amount = amt;
		send_count=s_cnt;
		hash();
	}
	public Transaction(Transaction o)
	{
		this.sender = o.sender;
		this.reciever = o.reciever;
		this.confirmations = o.confirmations;
		this.hash = new byte[o.hash.length];
		for (int i=0;i<o.hash.length;i++)
		{
			this.hash[i]=o.hash[i];
		}
	}

	public void inc_confirmations()
	{
		confirmations++;
	}
	public void hash()
	{
		hash = HashUtils.hash("" + sender + reciever + send_count + amount);
	}
	public byte[] getHash()
	{
		return hash;
	}
	public String toString()
	{
		return String.format("Send %d from %d to %d with hash %s (id %d conf# %d)", amount, sender, reciever, HashUtils.byteToHex(hash),send_count,confirmations);

	}
	public static int randInt(int m)
	{
		return (int) (Math.random() * m);
	}
	public static void main(String[] args)
	{
		Transaction carl = new Transaction(1, 2, 69, 1);
		// System.out.println(carl);

		int n = 4;
		Transaction t[] = new Transaction[n];
		for (int i=0;i<n;i++)
		{
			t[i] = new Transaction(randInt(10),randInt(10),randInt(10),randInt(10));
			System.out.println(t[i]);
		}
		MerkleTree m = new MerkleTree(t);
		System.out.println(m);

		// find out if p-256 is there lol
		// System.out.println(Security.getProviders("AlgorithmParameters.EC")[0].getService("AlgorithmParameters", "EC").getAttribute("SupportedCurves"));

	}


}