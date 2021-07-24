public class MultiMiner implements Runnable {
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
		long HASHES = 50000L;
		int found = 0;
		long start = System.currentTimeMillis();
		Block b = new Block(1, t, null);
		for (long i=0;i<HASHES;i++) {
			b.hash();
			if (b.less(15)) {
				System.out.println("THREAD : " + Thread.currentThread().getName() + "num: " + i + " HASH: " + HashUtils.byteToHex(b.getHash()));
				found++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("--THREAD: " + Thread.currentThread().getName());
		System.out.println(HASHES + " hashes took " + (end-start) + " ms, or about " + HASHES*1000.0/(end-start) + " hashes per second");
		System.out.println((end-start)/1000.0/found +" seconds per hash (" + found + ")");
		System.out.println("--THREAD: " + Thread.currentThread().getName());

	}
	public static void main(String[] args) {
		for (int i=0;i<10;i++) {
			Thread thread = new Thread(MultiMiner,""+i);
			thread.start();
		}
	}
}