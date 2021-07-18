import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils
{
	public static String byteToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
}