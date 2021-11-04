public abstract class ConcurrentThread extends Thread {
	protected SharedData sd;
	protected String name;
	protected Block currentBlock;
	// protected Lock minerLock;
	public ConcurrentThread(SharedData _sd, String _name, Block _currentBlock) {
		sd = _sd;
		name = _name;
		currentBlock = _currentBlock;
	}
	public abstract void run();
}