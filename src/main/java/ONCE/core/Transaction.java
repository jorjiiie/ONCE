package ONCE.core;

import java.math.BigInteger;
import java.io.Serializable;

/*
 * Ryan Zhu
 * Transactions have signers, senders, and reciever, as well as the amount and the signature
 * 
 */

public class Transaction implements Serializable {

	// should be final but i made pretty bad design choices earlier lol
	// can use the sha256 hash of the bigint as a string lol?
	// yes lol i think that would be just better, although sender still has to be in biginteger form (but its aids to read otherwise)
	private BigInteger reciever, sender;
	// public key of sender
	private BigInteger pubKey;
	private long amount;
	private BigInteger signature;
	private long id; // generates random number on sign, client will check whether or not that identifier has been used already
	// just use a timestamp ()
	private String hash;


	public Transaction(BigInteger _reciever, BigInteger _sender, long amt) {
		reciever = _reciever;
		sender = _sender;
		amount = amt;
	}
	public Transaction(BigInteger reciever, RSA sender, long amount) {
		this.reciever = reciever;
		this.sender = sender.getPublic();
		this.amount = amount;
		sign(sender);
	}
	public String toString() {
		return ""+reciever + "\n" + sender + "\n" + amount + "\n" + id + "\n" + hash;
	}

	public String str() {
		return ""+reciever+sender+amount+id;
	}

	public boolean sign(RSA _signer) {
		try {
			id = System.currentTimeMillis();
			signature = _signer.sign(HashUtils.sHash(str()));
			setHash();
			return true;
		} catch(Exception e) {
			System.out.println("hello!\n");
			return false;
		}
	}
	public BigInteger getReciever() {
		return reciever;
	}
	public BigInteger getSender() {
		return sender;
	}
	public long getAmount() {
		return amount;
	}

	public boolean verify() {
		if (signature == null || reciever == null || sender == null || hash == null)
			return false;
		RSA signer = new RSA(sender);
		return signer.verify(HashUtils.sHash(str()), signature);
	}
	public String getHash() {
		return hash;
	}
	public void setHash() {
		String str = "" + reciever + sender + amount + signature + id;
		hash = HashUtils.sHash(str);
	}
	public static void main(String[] args) {
		RSA joe = new RSA();
		RSA carl = new RSA();
		Transaction moneys = new Transaction(carl.getPublic(), joe.getPublic(), 69);
		moneys.sign(joe);

		System.out.println(moneys.verify());

		moneys.sign(carl);
		System.out.println(moneys.verify());
	}
}
