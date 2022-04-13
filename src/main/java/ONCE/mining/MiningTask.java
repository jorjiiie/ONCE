package ONCE.mining;

import ONCE.core.Block;
import ONCE.core.HashUtils;

import java.util.concurrent.Callable;
import java.util.Random;


public class MiningTask implements Callable<HashReturn> {

	public static final int HASHES_PER_CYCLE = 100000;
	private Random rnd;
	private String blockHeader;
	private long ts;
	// all share an atomic boolean for stopping, also will do n hashes per cycle before updating
	
	public MiningTask(String block, int id) {
		blockHeader = block;
		// seeds with thread number LOL
		rnd = new Random(id);
		ts = System.nanoTime();
	}


	@Override
	public HashReturn call() {
		for (int i=0;i<HASHES_PER_CYCLE; i++) {
			long salt = rnd.nextLong();
			byte[] hash = HashUtils.hash(blockHeader+ts+salt);
			if (!Block.lessThan(hash, Block.MINING_DIFFICULTY)) {
				return new HashReturn(salt, ts, true);
			}
		}
		return new HashReturn(0, 0, false);
	}
}