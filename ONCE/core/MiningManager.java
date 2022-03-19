package ONCE.core;

import java.util.ArrayList;

public class MiningManager extends Thread {
	private ArrayList<MiningThread> miners;
	private BlockPointer pointer;
	public MiningManager() {
		miners = new ArrayList<MiningThread>();
	}
	public void setBlock(Block b) {
		pointer.block = b;
	}
	public void addMiner(MiningThread thread) {
		miners.add(thread);
	}
	public void addMiners(int x) {
		for (int i=0;i<x;i++) {
			miners.add(new MiningThread(pointer));
		}
	}
}