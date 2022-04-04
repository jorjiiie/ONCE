package ONCE.core;


/*
import java.util.HashMap;
public class Blockchain {
	// will transition to a persistent hash map?
	private HashMap<String, Block> blockchain;
	public Blockchain() {
		// new blockchain wub a dub dub
		blockchain = new HashMap<String, Block>();
	}

	public int addBlock(Block b) {
		String prevBlock = b.getPrevious();

		
		if (blockchain.isEmpty() == false) {
			if (prevBlock == null) {
					return -1;
			}
			if (blockchain.get(prevBlock) == null) {
				return -1;
			}
		}

		// validate block
		if (b.verify() == false) {
			return -1;
		}
		// needs the less
		if (b.lessThan(MINING_DIFFICULTY) == false) {
			return -1;
		}
		blockchain.put(b.getBlockHash(), b);
		return b.depth;
	}

	public Block queryBlock(String hash) {
		return blockchain.get(hash);
	}
}
*/