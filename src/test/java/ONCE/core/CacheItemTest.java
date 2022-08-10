package ONCE.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CacheItemTest {
	@Test 
	public void cacheItemTest() {
		CacheItem<String, String> calzone = new CacheItem<String, String>("fred","carl",1L);
		CacheItem<String, String> pizza = new CacheItem<String, String>("bobby", "joeseph", 2L);
		CacheItem<String, String> kimchi = new CacheItem<String, String>("fred", "apple", 1L);
		CacheItem<String, String> apple = new CacheItem<String, String>("bobby", "joes", 2L);

		assertEquals(calzone.equals(pizza), false);
		assertEquals(calzone.equals(kimchi), true);
		assertEquals(pizza.equals(pizza), true);

		assertEquals((calzone.compareTo(pizza) < 0), true);


		CacheItem<String, String> asj = new CacheItem<>("999", "hi", 50L);
		CacheItem<String, String> akd = new CacheItem<>("000", "hi", 1L);

		assertEquals((asj.compareTo(akd)) < 0, false);
	}

}