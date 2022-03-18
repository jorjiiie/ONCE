package ONCE.core;

import java.math.BigInteger;
import java.util.Random;


/*
 * Ryan Zhu
 * February 21 2022
 * homebrew RSA that is definitely not a good idea but it's not like im doing this for practicality
 * Implements standard RSA, independent 2048 bit prime key generation & does the encrypt/decrypting stuff as well 
 */
public class RSA {
	public static final BigInteger RSA_E = new BigInteger("694201");
	private BigInteger n,d;
	public RSA() {
		//generate  keys
		
		Random rand = new Random();
		BigInteger p,q, phi;
		
		// just to force RSA_E to be coprime with phi
		while (true) {
			p = BigInteger.probablePrime(2048, rand);
			q = BigInteger.probablePrime(2048, rand);

			// just in case they are the same (lmao never happening but you can see why i dont get shit done when im concerned about stuff like this instead of actual things)
			while (p.equals(q)) {
				q = BigInteger.probablePrime(2048, rand);
			}

			n = p.multiply(q);			

			BigInteger tp, tq;
			tp = p.subtract(BigInteger.ONE);
			tq = q.subtract(BigInteger.ONE);
			phi = (tp.multiply(tq)).divide(tp.gcd(tq));
			if (!phi.mod(RSA_E).equals(BigInteger.ZERO))
				break;
		}
		d = RSA_E.modInverse(phi);
	}
	public RSA(BigInteger _n) {
		// for signature verfication
		n = _n;
	}
	public RSA(BigInteger _n, BigInteger _d) {
		// for initializing stuffs on clientside
		n = _n;
		d = _d;
	}
	public BigInteger encode(String message) {
		String hex = "";
		for (int i=0;i<message.length();i++) {
			hex += Integer.toHexString(message.charAt(i));
		}
		return (new BigInteger(hex, 16));
	}
	public BigInteger encrypt(BigInteger message) {
		//encrypt a message (m^e mod n)
		return message.modPow(RSA_E,n);
	}
	public BigInteger encrypt(String message) {
		return encrypt(encode(message));
	}
	public BigInteger decrypt(BigInteger message) {
		// decrypt (m^d mod n)
		return message.modPow(d,n);	
	}
	public BigInteger decrypt(String message) {
		return decrypt(encode(message));
	}
	public static String decimalToString(BigInteger decimal) {
		// every 2 = one hex
		String hexString = decimal.toString(16);
		String message = "";
		if (hexString.length() % 2 != 0) {
			// should def raise an error here but then we'd have to try/catch everything else
			// in this case, if message == error then its gonna be true lmao
			return "error";	
		}
		for (int i=0;i<hexString.length(); i+=2) {
			String s = hexString.substring(i,i+2);
			int c = Integer.valueOf(s,16);
			message += (char) c;
		}
		return message;
	}
	public boolean verify(String message, BigInteger signature) {
		// verify signature
		return HashUtils.sHash(message).equals(decimalToString(encrypt(signature)));
	}
	public BigInteger sign(String message) {
		if (d!=null) {
			return decrypt(HashUtils.sHash(message));

		} else {
			throw new java.lang.RuntimeException("Trying to sign without private key");
		}
	}
	public BigInteger getPublic() {
		return n;
	}
	public static void main(String[] args) {
		RSA joe = new RSA();
		System.out.println(joe.sign("Hello World!"));
		System.out.println(joe.encrypt(joe.sign("Hello World!")));
		System.out.println(joe.verify("Hello World!", joe.sign("Hello World!")));
	}
}
