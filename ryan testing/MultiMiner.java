public class MultiMiner implements Runnable {
	private long HASHES;
	public MultiMiner(long n) {
		HASHES = n;
	}
	public void run() {
		User carl = new User();
		carl.initUser();
		User joe = new User();	
		joe.initUser();

		int n = 5;
		Transaction t[] = new Transaction[n];
		for (int i=0;i<n;i++) {
			t[i] = carl.sendTo(joe.getPublicKey(),5);
			// System.out.println(t[i]);
		}


		// uwu mining
		int found = 0;
		long start = System.currentTimeMillis();
		Block b = new Block(1, t, null);
		for (long i=0;i<HASHES;i++) {
			b.hash();
			if (b.less(20)) {
				// System.out.println("THREAD : " + Thread.currentThread().getName() + "num: " + i + " HASH: " + HashUtils.byteToHex(b.getHash()));
				found++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("THREAD: " + Thread.currentThread().getName() + " TOOK " + (end-start) + " ms for " + HASHES);

	}
	public static void main(String[] args) {
		long TOTAL_HASHES = 20000000L;
		int HASHES = 500000;
		// best is actual number of threads
		// heavily saturates cpu
		int num_threads = 8;
		long start = System.currentTimeMillis();
		for (int i=0;i<num_threads;i++) {
			Thread thread = new Thread(new MultiMiner(TOTAL_HASHES/num_threads),""+i);
			thread.start();
		}
		long end = System.currentTimeMillis();
	}
}