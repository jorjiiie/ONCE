public class MyRunnable implements Runnable {
	public void run() {
		
	}
	public static void main(String[] args) {
		MyRunnable runner = new MyRunnable();
		Thread thread1 = new Thread(runner);
		thread1.start();
	}
}