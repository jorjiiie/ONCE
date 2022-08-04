package ONCE.core

import java.io.Serializable;

public class BlockRecord implements Serializable {
	private Block block;
	private int confirmations = 1;
	private String firstChild = null;

	public BlockRecord(Block b) {
		block = b;
	}
	public BlockRecord(BlockRecord br) {
		block = br.block;
		confirmations = br.confirmations;
		directChildren = br.firstChild;
	}

	public boolean hasChild(String s) {
		if (firstChild == null || firstChild.equals(s)) {
			firstChild = s;
			return false;
		}
		return true;
	}
	public boolean hasChild(Block b) {
		return hasChild(b.getBlockHash());
	}

	public void addConfirmation() {
		confirmations++;
	}

}