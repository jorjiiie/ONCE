import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// do we serialize this idk
public class User 
{
	// will remove later obviously
	private int id;

	private static int num=0; 
	private PublicKey publicKey;
	// LOL i think we can do this bc its local
	private PrivateKey privateKey;
	private int actions = 0;
	public User()
	{
		// do nothing?
		id = ++num;
	}
	public void initUser() 
	{
		// probably have to switch to bouncycastle bc we don't want to save both pub and priv, just want priv and derive public from it

		// generate a keypair
		try {
		 	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");

		 	// nist p-256 bc it's available on all implementations LOL
		 	// maybe change but honestly its not necessary
	        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

	        // generate pair
	        KeyPair kp = keyGen.generateKeyPair();
	        publicKey = kp.getPublic();
	        privateKey = kp.getPrivate();
		} catch(Exception e) {
			throw new java.lang.RuntimeException("Initialization of user failed, most likely because secp256r1 (NIST P-256) not available");
		}
		// System.out.println(kp);
		// System.out.println(publicKey + "\n" + privateKey);

	}

	private byte[] sign(String m) 
	{
		try {
			Signature ecdsa = Signature.getInstance("SHA256withECDSA");

			ecdsa.initSign(privateKey);

			ecdsa.update(m.getBytes("UTF-8"));

			return ecdsa.sign();
		} catch(Exception e) {
			throw new java.lang.RuntimeException("ECDSA signature failed, check if SHA256withECDSA is available");
		}
	}
	public boolean verify(byte[] sig, String m) 
	{
		try {
			Signature ecdsa = Signature.getInstance("SHA256withECDSA");
			ecdsa.initVerify(publicKey);

			ecdsa.update(m.getBytes("UTF-8"));

			return ecdsa.verify(sig);
		} catch (Exception e) {
			throw new java.lang.RuntimeException("ECDSA verification failed, check if SHA256withECDSA is available");
		}
	}
	public byte[] sign(Transaction t)
	{
		// sign hex version of transaction hash
		
		try {
			String m = HashUtils.byteToHex(t.getHash());
			byte[] ret;

			ret = sign(m);
			return ret;
		} catch (Exception e) {
			throw new java.lang.RuntimeException("Transaction couldn't be signed");
		}
		
	}
	public PublicKey getPublicKey() {
		return publicKey;
	}
	public Transaction sendTo(User other, int amount) {
		// won't check if user has enough bc it's stored in blockchain not in the class
		// this func is just for testing
		Transaction t = new Transaction(publicKey, other.publicKey,amount,++actions);

		t.setSignature(sign(t));
		return t;
	}
	public Transaction sendTo(PublicKey address, int amount) {

		Transaction t = new Transaction(publicKey, address, amount, ++actions);

		t.setSignature(sign(t));
		return t;
	}
	public static void main(String[] args)
	{
		User joe = new User();
		joe.initUser();
		byte[] carl;
		carl = joe.sign("yo mamam fat");
		System.out.println(HashUtils.byteToHex(carl));
		System.out.println("joe signed message?: " + joe.verify(carl,"yo mamam fat"));
		
	}
}