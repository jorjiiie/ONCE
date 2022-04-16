package ONCE.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.math.BigInteger;
public class UtilTest {
	@Test 
	public void testHextoBytes() {
		// Block b = new Block(null, false);
		String bytes = HashUtils.hexToByte("1237ef");
		assertEquals("000100100011011111101111", bytes);
		String bytes2 = HashUtils.hexToByte("aeee69420");
		assertEquals("101011101110111001101001010000100000", bytes2);
	}

	@Test 
	public void byteToHex() {
		String b = "0110100001101001";
		byte[] bval = new BigInteger(b, 2).toByteArray();

		String hex = HashUtils.byteToHex(bval);
		assertEquals("6869",hex);

		// this method follows two's compliment soo
		// just be careful
		String x = "001010100100101010010101";
		byte[] bval2 = new BigInteger(x, 2).toByteArray();

		String hex2 = HashUtils.byteToHex(bval2);
		assertEquals("2a4a95",hex2);
	}
	@Test 
	public void hashing() {
		// just test the 4 hash functions

	}
}