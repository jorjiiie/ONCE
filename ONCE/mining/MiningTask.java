package ONCE.mining;

import ONCE.core.Block;
import ONCE.core.HashUtils;

import java.util.concurrent.Callable;
import java.util.Random;


public class MiningTask implements Callable<HashReturn> {

	public static final int HASHES_PER_CYCLE = 100000;
	private Random rnd;
	private String blockHeader;

	// all share an atomic boolean for stopping, also will do n hashes per cycle before updating
	
	public MiningTask(String block, int id) {
		blockHeader = block;
		// seeds with thread number LOL
		rnd = new Random(id);
	}


	@Override
	public HashReturn call() {
		for (int i=0;i<HASHES_PER_CYCLE; i++) {
			long salt = rnd.nextLong();
			byte[] hash = HashUtils.hash(blockHeader+salt);
			if (!Block.lessThan(hash, 15)) {
				return new HashReturn(salt, true);
			}
		}
		return new HashReturn(0, false);
	}
}