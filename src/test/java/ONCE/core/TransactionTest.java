package ONCE.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TransactionTest {
	@Test 
	public void testTransaction() {
		System.out.println("hi");
		String str = "joe";
		assertEquals("joe",str);
	}
}