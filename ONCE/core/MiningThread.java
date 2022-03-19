package ONCE.core;

public class MiningThread extends Thread {
	public static final int HASHES_PER_CYCLE = 100000;
	private BlockPointer pointer;
	private boolean stopSignal = false;
	public MiningThread(BlockPointer _pointer) {
		pointer = _pointer;
	}
	@Override 
	public void interrupt() {
		stopSignal = true;
	}
	public void mine() {
		while (stopSignal != true) {
			for (int i=0;i<HASHES_PER_CYCLE;i++) {
				pointer.block.hash();
				if (pointer.block.lessThan(Block.MINING_DIFFICULTY)) {
					// we got something!
					// notify main thread somehow
				}
			}
		}
	}
}