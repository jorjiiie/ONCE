import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class User 
{
	// will remove later obviously
	private int id;

	private static int num=0; 
	private PublicKey publicKey;
	// LOL i think we can do this bc its local
	private PrivateKey privateKey;

	public User()
	{
		// do nothing?
		id = ++num;
	}
	public void initUser() throws Exception
	{
		// probably have to switch to bouncycastle bc we don't want to save both pub and priv, just want priv and derive public from it

		// generate a keypair
	 	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");

	 	// nist p-256 bc it's available on all implementations LOL
	 	// maybe change but honestly its not necessary
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        // generate pair
        KeyPair kp = keyGen.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
	
		System.out.println(kp);
		System.out.println(publicKey + "\n" + privateKey);

	}

	private byte[] sign(String m) throws Exception
	{
		Signature ecdsa = Signature.getInstance("SHA256withECDSA");

		ecdsa.initSign(privateKey);

		ecdsa.update(m.getBytes("UTF-8"));

		return ecdsa.sign();
	}
	public boolean verify(byte[] sig, String m) throws Exception
	{
		Signature ecdsa = Signature.getInstance("SHA256withECDSA");
		ecdsa.initVerify(publicKey);

		ecdsa.update(m.getBytes("UTF-8"));

		return ecdsa.verify(sig);
	}
	public static void main(String[] args)
	{
		User joe = new User();
		try
		{
			joe.initUser();
		}
		catch(Exception e) {
			System.out.println("rip");
		}
		byte[] carl;
		try{
			carl = joe.sign("yo mamam fat");
			System.out.println(HashUtils.byteToHex(carl));
			System.out.println("joe signed message?: " + joe.verify(carl,"yo mamam fat"));
		}
		catch(Exception e)
		{
			System.out.println("rip2");
		}
	}
}