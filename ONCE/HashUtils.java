import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
/*
 * Ryan Zhu
 * Some hashing utilities like hashing + hexifying
 *
 */
public class HashUtils
{
	static HashMap<Character, String> hex;

	public static String hexToByte(String s) {
		// yes I know it returns a string
		if (hex == null) {
			hex = new HashMap<Character, String>();
			hex.put('0', "0000");
			hex.put('1', "0001");
			hex.put('2', "0010");
			hex.put('3', "0011");
			hex.put('4', "0100");
			hex.put('5', "0101");
			hex.put('6', "0110");
			hex.put('7', "0111");
			hex.put('8', "1000");
			hex.put('9', "1001");
			hex.put('a', "1010");
			hex.put('b', "1011");
			hex.put('c', "1100");
			hex.put('d', "1101");
			hex.put('e', "1110");
			hex.put('f', "1111");
		}
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<s.length();i++) {
			sb.append(hex.get(s.charAt(i)));
		}
		return sb.toString();
	}

	public static String byteToHex(byte[] bytes)
	{
		try {
			StringBuilder sb = new StringBuilder();
	        for (byte b : bytes) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	    } catch (NullPointerException e) {
	    	return "null";
	    }
	}
	public static byte[] hash(byte[] input) 
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException e)
		{
			throw new IllegalArgumentException(e);
		}
		return md.digest(input);
	}
	public static byte[] hash(String s)
	{
		return hash(s.getBytes(StandardCharsets.UTF_8));
	}
	public static String sHash(String s) {
		return byteToHex(hash(s));
	}
}

