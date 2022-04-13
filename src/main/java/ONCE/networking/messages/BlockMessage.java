package ONCE.networking.messages;

import ONCE.core.*;

import java.io.Serializable;

// temp thing this probably shouldn't be like this
public class BlockMessage implements Serializable, Payload {
	public final Block block;

	public BlockMessage(Block b) {
		block = b;
	}
	public String checksum() {
		return block.getBlockHash();
	}
}