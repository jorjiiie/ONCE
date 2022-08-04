package ONCE.core;

import java.util.TreeMap;
import java.util.HashMap;

public class Cache<K, V> extends HashMap<K, V>{
	// maybe dont make final since different obj have different memory footprints but whatever right
    private static final int CACHE_LIMIT = 10;

	private TreeMap<Long, K> pq = new TreeMap<Long, K>();
	private Long itemCount = 0L;

	public V query(K key) {
		V value = super.get(key);
		if (value == null)
			return null;
		// update this to the most recently accessed so we don't clear it immediately
		
		return super.get(key);
	}

	public boolean add(K key, V value) {
		if (super.get(key) != null) 
			return false;
		// add the object & clear the next to be cleared if we are above the cache limit
		itemCount++;
		super.put(key, value);
		pq.put(itemCount, key);
		if (pq.size() > Cache.CACHE_LIMIT) {

			Long toPop = pq.firstKey();
			System.out.println("REMOVING ENTRY");
			super.remove(pq.get(toPop));
			pq.remove(toPop);
		}
		return true;
	}


	public static void main(String[] args) {
		Cache<String, Integer> thing = new Cache<String, Integer>();
		String s = "AJSLDKASJDK";
		String k = "JJJJJ";
		thing.add(s, 69);
		thing.add(k, 40);
		String[] arr = new String[30];
		for (int i=0;i<20;i++) {
			arr[i] = String.valueOf((int)(Math.random() * 10000 + 1));
			thing.add(arr[i], (int)(Math.random() * 50 + 1));
		}

		System.out.println("ASJDLKAS");
	}
}