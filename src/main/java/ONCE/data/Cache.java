package ONCE.data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;



// for testing
import java.util.HashSet;

// can just use treeset tbh but im missing a few braincells
class Cache<K extends Comparable<? super K>, V> {
	// can do LRU with a doublely linked list :sob:

	public static final int DEFAULT_CACHE_LIMIT = 1000;
    private final int CACHE_LIMIT;

    // this is so closely tied to the actual implementation that i have to essentially rewrite this whole thing for the doubly linked list one
    // will test later
	private ConcurrentHashMap<K, CacheItem<K, V> > references = new ConcurrentHashMap<>();

	private ReentrantReadWriteLock lockPair = new ReentrantReadWriteLock();
	private Lock readLock = lockPair.readLock();
	private Lock writeLock = lockPair.writeLock();

	private AVLNode root;
	private int avlSize = 0;

	public Cache() {
		CACHE_LIMIT = DEFAULT_CACHE_LIMIT;
	}

	public Cache(int k) {
		CACHE_LIMIT = k;
	} 
	
	// grab the read lock BEFORE doing this
	public AVLNode avlQuery(CacheItem<K, V> item) {
		AVLNode currentNode = root;
		while (currentNode != null) {
			int res = item.compareTo(currentNode.data);
			if (res == 0) {
				return currentNode;
			}
			if (res < 0) {
				currentNode = currentNode.leftChild;
			} else {
				currentNode = currentNode.rightChild;
			}
		}
		// current node is null, no hit
		return null;
	}
	public V query(K key) {
		// query in 
		if (key == null)
			return null;
		CacheItem<K, V> lookup = references.get(key);

		if (lookup == null)
			return null;
		AVLNode cacheHit;
		try {
			readLock.lock();
			cacheHit = avlQuery(lookup);
		} finally {
			readLock.unlock();
		}

		assert cacheHit != null : "cachehit was somehow null";

		remove(cacheHit);

		lookup.priority++;

		try {
			writeLock.lock();
			root = avlInsert(root, lookup);
		} finally {
			writeLock.unlock();
		}

		return lookup.value;
	}
	public void insert(K key, V value) {
		// this is only called if the cache query misses

		CacheItem<K, V> item = new CacheItem<>(key, value, 1L);

		try {
			writeLock.lock();
			root = avlInsert(root, item);
		} finally {
			writeLock.unlock();
		}

		references.put(key, item);

		// we may get stuck in a loop where no new keys can be added but its highly unlikely i pray (one can be stuck though so its a bit problematic due to sorting)
		if (avlSize <= CACHE_LIMIT) {
			return;
		}
		AVLNode firstNode;
		try {
			readLock.lock();
			firstNode = avlFirstItem(root); 
		} finally {
			readLock.unlock();
		}
		
		references.remove(firstNode.data.key);

		remove(firstNode);
		
		
	}
	// not public since this should not be called from the outside
	private void remove(K key, V val) {
		// for testing all things are 1 priority, so will create a cache item <k, "hi", 1L>
		CacheItem<K, V> lookup = new CacheItem<K, V>(key, val, 1L);

		try {
			writeLock.lock();
			root = avlRemove(root, lookup);
		} finally {
			writeLock.unlock();
		}
	}
	private void remove(AVLNode node) {
		try {
			writeLock.lock();
			root = avlRemove(root, node.data);
		} finally {
			writeLock.unlock();
		}
	}

	// helper for avlInsert and avlRemove
	private AVLNode avlUpdate(AVLNode node) {
		node.updateHeight();
		
		int bf = node.getBalanceFactor();

		// if bf != +/- 2 then I messed up somewhere 
		assert (bf >= -2 && bf <= 2) : "balance factor is screwed up";

		if (bf == 2) {
			// right heavy

			int bf2 = node.rightChild.getBalanceFactor();

			if (bf2 < 0) {
				// right left
				node.rightChild = avlRotateRight(node.rightChild);
				return avlRotateLeft(node);
			} else {
				// right right
				return avlRotateLeft(node);
			}
		} else if (bf == -2) {
			// left heavy

			int bf2 = node.leftChild.getBalanceFactor();

			if (bf2 <= 0) {
				// left left
				return avlRotateRight(node);
			} else {
				// left right
				node.leftChild = avlRotateLeft(node.leftChild);
				return avlRotateRight(node);
			}
		}

		return node;
	}
	// returns the subtree root
	// ex. at the first level, will return the root.
	// at a height of h, will return the subtree rooted at x 
	// does this so we can perform rotations and not have the parent pointer
	private AVLNode avlInsert(AVLNode node, CacheItem<K, V> item) {
		if (node == null)  {
			avlSize++;
			return new AVLNode(item);
		}
		
		int res = item.compareTo(node.data);

		assert res != 0 : "item inserted was somehow already in AVLTree";

		if (res < 0) {
			// go left
			node.leftChild = avlInsert(node.leftChild, item);
		} else {
			// go right
			node.rightChild = avlInsert(node.rightChild, item);
		}

		return avlUpdate(node);
	}
	// very similar to avlInsert, except on the first operation
	private AVLNode avlRemove(AVLNode node, CacheItem<K, V> item) {
		//node is ALWAYS in the tree if we call this since it was just queried for

		int res = item.compareTo(node.data);
		if (res == 0) {
			if (node.leftChild == null) {
				avlSize--;
				return node.rightChild;

			} else if (node.rightChild == null) {
				avlSize--;
				return node.leftChild;
			} else {
				// replace with the lower bound on element
				AVLNode lowerBound = avlFirstItem(node.rightChild);
				node.data = lowerBound.data;

				node.rightChild = avlRemove(node.rightChild, lowerBound.data);
			}
		} else if (res < 0) {
			node.leftChild = avlRemove(node.leftChild, item);
		} else {
			node.rightChild = avlRemove(node.rightChild, item);
		}

		return avlUpdate(node);
	}
	private AVLNode avlFirstItem(AVLNode node) {
		AVLNode current = node;
		while (current.leftChild != null) {
			current = current.leftChild;
		}
		return current;

	}
	private AVLNode avlLastItem() {

		return null; // right most item
	}
	private int getHeight(AVLNode node) {
		if (node == null)
			return 0;
		return node.height;
	}

	// returns the new highest node (new parent)
	private AVLNode avlRotateLeft(AVLNode node) {
		// rotates the right node to become the parent
		AVLNode original = node;
		AVLNode right = original.rightChild;

		assert right != null : "Right child is NULL on a left rotation";

		original.rightChild = right.leftChild;
		right.leftChild = original;


		original.updateHeight();
		right.updateHeight();

		return right;
	}

	private AVLNode avlRotateRight(AVLNode node) {
		// rotates the left node to become the parent
		AVLNode original = node;
		AVLNode left = original.leftChild;

		assert left != null : "left child is NULL on a right rotation";

		original.leftChild = left.rightChild;
		left.rightChild = original;

		original.updateHeight();
		left.updateHeight();

		return left;
	}

	// **** DEBUGGING METHODS ****
	private void printLeftRight(AVLNode node) {
		// preorder i think
		if (node == null)
			return;
		
		printLeftRight(node.leftChild);
		System.out.println(node);

		printLeftRight(node.rightChild);
	}

	private int getMaxHeight(AVLNode node) {
		if (node == null)
			return 0;
		int childmx =  Math.max(getMaxHeight(node.leftChild), getMaxHeight(node.rightChild));
		node.updateHeight();
		return Math.max(node.height, childmx);
	}
	
	private static CacheItem<String, String> genCacheItem(String a, String b, Long k) {
		return new CacheItem<String, String>(a,b,k);
	}
	private int getMinLeafHeight(AVLNode node, int d) {
		if (node == null) {
			// potentially cause bugs but who cares
			return 694206942;
		}
		if (node.leftChild == null && node.rightChild == null) {
			return d;
		}
		return Math.min(getMinLeafHeight(node.leftChild, d + 1), getMinLeafHeight(node.rightChild, d + 1));
	}
	private boolean checkBalance(AVLNode node) {
		if (node == null)
			return true;

		boolean ret = true;
		ret &= checkBalance(node.leftChild) & checkBalance(node.rightChild);
		node.updateHeight();
		int bf = node.getBalanceFactor();
		return (bf >= -2 && bf <= 2);
	}
	private boolean checkIntegrity(AVLNode node) {
		if (node == null)
			return true;
		if (node.data == null) {
			return false;
		}
		if (node.data.key == null || node.data.value == null || node.data.priority == null)
			return false;
		return checkIntegrity(node.rightChild) && checkIntegrity(node.leftChild);
	}
	// END OF **** DEBUGGING METHODS ****

	private class AVLNode {
		CacheItem<K, V> data;
		AVLNode leftChild, rightChild;
		int height = 1;
		AVLNode(CacheItem<K, V> data) {
			this.data = data;
		}
		void updateHeight() {
			height = 1 + Math.max((leftChild == null)?0:leftChild.height, (rightChild == null)?0:rightChild.height);
		}
		int getBalanceFactor() {
			return (rightChild == null?0:rightChild.height) - (leftChild==null?0:leftChild.height);
		}
		String idString() {
			return getClass().getName() + '@' + Integer.toHexString(hashCode());
		}
		public String toString() {
			String lString = (leftChild == null)?"null":leftChild.idString();
			String rString = (rightChild==null)?"null":rightChild.idString();

			String ret = idString() + " " + data + " left: " + lString + " right: " + rString + " h: " + height + " bf: " + getBalanceFactor();
			return ret;
		}
	}


	// to be 100% honest i have no clue how to test this thing with assert statements because that seems like
	// massive ass to have to keep references and work through it by hand and HARDCODE every assert
	// ill do it by hand 100 times thank you very much 
	public static void test4() {
		// debugging test
		Cache<String, String> testCache = new Cache<String, String>();
		HashSet<String> used = new HashSet<String>();

		int sz = 1000;
		int cnt = 0;
		while (cnt < sz) {
			String k = Integer.toString((int)(Math.random() * sz*100));
			if (used.contains(k))
				continue;
			used.add(k);
			testCache.insert(k,"hi");
			cnt++;		
			System.out.println("AJSKDLJASKLDJAKLSDJALSD\n");
		}
		System.out.println(testCache.checkIntegrity(testCache.root));
		for (String s : used) {
			testCache.remove(s, "hi");
			System.out.println(s+" " + testCache.checkBalance(testCache.root) + " : " + testCache.getMaxHeight(testCache.root));
		}
		// testCache.printLeftRight(testCache.root);
	}
	public static void test3() {
		// access test (queries + updating priorities)
		Cache<String, String> testCache = new Cache<String, String>();
		HashSet<String> used = new HashSet<String>();

		int sz = 1000;
		int cnt = 0;
		while (cnt < sz) {
			String k = Integer.toString((int)(Math.random() * sz*100));
			if (used.contains(k))
				continue;
			used.add(k);
			testCache.insert(k,"hi");
			cnt++;		
			for (int i=0;i<500;i++) {
				String k2 = Integer.toString((int)(Math.random() * 1e5));
				testCache.query(k2);
			}
		}
		testCache.printLeftRight(testCache.root);
	}

	// random vs ordered addition seems like its within 1 height
	public static void test2() {
		// speed test
		Cache<String, String> testCache = new Cache<String, String>();
		Cache<String, String> testCache2 = new Cache<String, String>();

		HashSet<String> used = new HashSet<String>();

		int sz = 1000;
		
		for (int i=0;i<sz;i++) {
			testCache.insert(Integer.toString(i), "hi");
		}
		System.out.println("done with inorder");
		int cnt = 0;
		while (cnt < sz) {
			String k = Integer.toString((int)(Math.random() * 1e9));
			if (used.contains(k))
				continue;
			used.add(k);
			testCache2.insert(k,"hi");
			cnt++;
		}

		System.out.println("Max height is (1) " + testCache.getMaxHeight(testCache.root) + " " + testCache.getMinLeafHeight(testCache.root,0));
		// testCache.printLeftRight(testCache.root);
		// System.out.println(testCache.root);
		System.out.println(testCache.checkBalance(testCache.root));

		System.out.println("Max height is (2) " + testCache2.getMaxHeight(testCache2.root));
		// testCache2.printLeftRight(testCache2.root);
		System.out.println(testCache2.checkBalance(testCache2.root));


		// this does not work if sz > cache_limit, so be aware of that since we assume it is there
		
		for (int i=0;i<sz;i++) {
			testCache.remove(Integer.toString(i), "hi");
			System.out.println("" + testCache.checkBalance(testCache.root) + " : " + testCache.getMaxHeight(testCache.root));
		}
		int found = 0;
		for (String s : used) {
			found += (testCache2.avlQuery(testCache2.references.get(s))==null)?0:1;
			testCache2.remove(s, "Hi");
			System.out.println(s+" " + testCache2.checkBalance(testCache2.root) + " : " + testCache2.getMaxHeight(testCache2.root));
		}
		System.out.println("hits : " + found);
	}
	public static void test1() {
		Cache<String, String> testCache = new Cache<>();

		CacheItem<String, String> a = genCacheItem("hi", "fred", 1L);
		CacheItem<String, String> b = genCacheItem("carl", "fred", 2L);

		System.out.println(a + "\n" + b);
		// testCache.avlInsert(testCache.root, a);
		// testCache.avlInsert(testCache.root, b);
		testCache.insert("hi", "fred");
		System.out.println("ROOT: " + testCache.root);

		testCache.insert("bbc", "carl");
		System.out.println("ROOT: " + testCache.root);

		testCache.insert("ASDASD", "asd");
		System.out.println("ROOT: " + testCache.root);

		testCache.insert("coolio", "a");
		System.out.println("ROOT: " + testCache.root);

		testCache.printLeftRight(testCache.root);
		System.out.println("ROOT: " + testCache.root);
	}
	public static void main(String[] args) {
		// test1();
		// test2();
		// test3();
		// test4();


	}

}

