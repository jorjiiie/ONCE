import java.security.*;

public class Transaction extends CRYPTO implements java.io.Serializable {

	public static final int CONFIRMATION_THRESHOLD = 1;
	private PublicKey sender,reciever;
	private int send_count; // how many times the sender has made a transaction lol this is so bad
	private long amount;

	// somehow implement signature too idk how to do this yet
	// will auto reject if its not confirmed but it wont check outside of that
	// clients job^


	private int confirmations;
	private byte[] hash;
	private byte[] sig;


	public Transaction() {
		super(101010);
		// joe
	}
	public Transaction(PublicKey s, PublicKey r, int amt, int s_cnt) {
		super(101010);
		sender = s;
		reciever = r;
		amount = amt;
		send_count=s_cnt;
		hash();
	}
	public Transaction(Transaction o) {
		// i dont think we need this
		super(101010);
		this.sender = o.sender;
		this.reciever = o.reciever;
		this.confirmations = o.confirmations;
		this.hash = new byte[o.hash.length];
		for (int i=0;i<o.hash.length;i++)
		{
			this.hash[i]=o.hash[i];
		}
	}
	public void inc_confirmations() {
		confirmations++;
	}
	public void hash() {
		hash = HashUtils.hash("" + sender + reciever + send_count + amount);
	}
	public byte[] getHash() {
		return hash;
	}
	public String toString() {

		return "Sender: " + sender + "\nReciever: " + reciever + "\nAmount: " + amount + "\nSend ID: " + send_count + "\nHash: " + HashUtils.byteToHex(hash) + "\nSignature: " + HashUtils.byteToHex(sig);
	}
	public boolean setSignature(byte[] signature) {
		if (sig!=null) return false;
		sig = signature;
		return true;
	}
	public static void main(String[] args) {

		User carl = new User();
		carl.initUser();
		User joe = new User();	
		joe.initUser();

		Transaction t = carl.sendTo(joe,15);
		System.out.println(t);
	}


}