package ONCE.data;

import java.util.HashSet;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class CacheTest extends Cache {

	@Test
	public void CacheQueryTest() {
		Cache<String, Integer> testCache = new Cache<String, Integer>();
		HashMap<String, Integer> used = new HashMap<String, Integer>();

		int sz = 10000;
		int cnt = 0;
		int hits = 0;
		int queries = 0;
		while (cnt < sz) {
			String k = Integer.toString((int)(Math.random() * sz*100));
			if (used.get(k) != null)
				continue;
			Integer abc = (int) (Math.random() * 2392333);
			used.put(k,abc);
			testCache.insert(k,abc);
			cnt++;		
			for (int i=0;i<500;i++) {
				String k2 = Integer.toString((int)(Math.random() * 1e5));

				if (used.get(k2) != null)
					queries++;

				Integer res = testCache.query(k2);
				if (res != null) {
					hits++;
					assertEquals(res, used.get(k2));
				}
			}
		}
		System.out.println("HITS: " + hits + " QUERIES: " + queries + " RATE: " + hits*1.0/queries);
		// testCache.printLeftRight(testCache.root);
	}
}