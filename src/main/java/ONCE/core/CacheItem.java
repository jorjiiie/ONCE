package ONCE.core;
// sort by value
class CacheItem<K extends Comparable<? super K>, V > implements Comparable<CacheItem<K, V> > {
	K key;
	V value;
	Long priority;

	public CacheItem(K k, V v, Long p) {
		key = k;
		value = v;
		priority = p;
	}
	public boolean equals(CacheItem<K, V > other) {
		// compare priorities first, then by key
		// guarenteed no keys are identical since block hashes are ALWAYS different
		// can check with a giant hashmap tbh it would only take up like a couple mb with thousands of blocks, def less memory than storing every block in memory!
		if (priority.equals(other.priority))
			return true;
		return key.equals(other.key);
	}

	public int compareTo(CacheItem<K, V> other) {
		int comp = priority.compareTo(other.priority);
		if (comp != 0)
			return comp;
		return key.compareTo(other.key);
	}
	public String toString() {
		return "[" + key.toString() + ", " + value.toString() + ", " + priority.toString() + "]";
	}
}