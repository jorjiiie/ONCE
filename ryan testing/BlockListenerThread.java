public class BlockListenerThread extends ConcurrentThread{
	private Block newBlock;

	public BlockListenerThread(SharedData sd, String name, Block block) {
		super(sd,name,block);
	}

	public void run() {
		// always running?
		// sleep for 500 ms
		while (true) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// interrupted but this shuoldnt?
				System.out.println(e);

			}
			boolean recievedBlock = true;
			if (recievedBlock) {
				sd.flagA.getAndSet()
			}
		}
	}
}