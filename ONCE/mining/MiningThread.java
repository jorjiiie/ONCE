package ONCE.mining;

import ONCE.core.Block;
import ONCE.core.HashUtils;

import java.util.Random;
public class MiningThread extends Thread {

	public static final int HASHES_PER_CYCLE = 10000;
	private Random rnd;
	private String blockHeader;
	private boolean stopSignal = false;
	public MiningThread(String block, int id) {
		blockHeader = block;
		// seeds with thread number LOL
		rnd = new Random(id);
	}
	@Override 
	public void interrupt() {
		stopSignal = true;
	}
	public void setBlock(String block) {
		blockHeader = block;
	}

	@Override
	public void run() {
		for (int i=0;i<HASHES_PER_CYCLE; i++) {
			long salt = rnd.nextLong();
			byte[] hash = HashUtils.hash(blockHeader+salt);
			if (!Block.lessThan(hash, 15)) {
				MiningManager.self.foundHash(hash, salt);
			}
		}
	}
}