package ONCE.core;

import java.io.Serializable;

public class BlockRecord implements Serializable {

	Block block;
	int children = 0;
	BalanceSheet sheet;

	public BlockRecord(Block b) {
		block = b;
		sheet = new BalanceSheet(block);
	}
	public BlockRecord(BlockRecord br) {
		block = br.block;
		children = br.children;
		sheet = br.sheet;
	}

	public void addChildren() {
		children++;
	}
	public int getChildren() {
		return children;
	}
	public void setJunction(BlockRecord prev) {
		sheet.setJunctionSheet(prev.block.getBlockHash());
		sheet.extendPreviousSheet(prev.sheet);
	}


}