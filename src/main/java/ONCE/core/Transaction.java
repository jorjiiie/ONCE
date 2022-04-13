package ONCE.core;

import java.math.BigInteger;
import java.io.Serializable;

/*
 * Ryan Zhu
 * Transactions have signers, senders, and reciever, as well as the amount and the signature
 * 
 */

public class Transaction implements Serializable {

	// transactions also must have a identifier otherwise you could reuse transactions
	private BigInteger reciever, sender;
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
	public String toString() {
		return ""+reciever + "\n" + sender + "\n" + amount + "\n" + id;
	}
	// needs the private key which will not be here
	
	private void generateID() {
		id = (long) (Math.random() * (1<<30)) * (long)(Math.random() * (1<<30)); 
	}
	public boolean sign(RSA _signer) {
		try {
			id = System.currentTimeMillis();
			signature = _signer.sign(HashUtils.sHash(toString()));
			return true;
		} catch(Exception e) {
			System.out.println("hello!\n");
			return false;
		}
	}

	public boolean verify() {
		if (signature == null)
			return false;
		RSA signer = new RSA(sender);
		return signer.verify(HashUtils.sHash(toString()), signature);
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
