public class BlockMessage extends MessageHeader {
	public Block block;
	public BlockMessage(Block _block) {
		super(2);
		block = _block;
	}
}