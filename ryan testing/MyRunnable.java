public class MyRunnable implements Runnable {

	SYNC test;

	public MyRunnable(SYNC in) {
		test = in;
	}
	public void run() {
		for (int i=0;i<50000;i++) {
			int k = i + i;
		}
		synchronized(test) {
			if (!test.flag) { 
				test.flag = true;
				test.claimed = Thread.currentThread().getName();
				System.out.println("joe" + " " + test.flag + " " + test.claimed);
			}
			// System.out.println("joe\n");
		}
	}
	static class SYNC {
		public volatile boolean flag = false;
		public volatile String claimed;
	}
	public static void main(String[] args) {


		SYNC joe = new SYNC();

		for (int i=0;i<5;i++) {
			MyRunnable runner = new MyRunnable(joe);

			Thread thread1 = new Thread(runner);
			thread1.start();
		
		}
		System.out.println(joe.flag + " " + joe.claimed);
	}
}