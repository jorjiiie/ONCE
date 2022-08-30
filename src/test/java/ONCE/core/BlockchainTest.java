package ONCE.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests blockchains and whatnot
 * Tests adding transactions, adding/removing blocks, etc etc
 * this is of vital importance!!!
 * @author jorjiiie
 */
public class BlockchainTest {
	@Test 
	public void testTransaction() {
		System.out.println("hi");
		String str = "joe";
		assertEquals("joe",str);
	}

	// test genesis block + some cool blocks
	@Test
	public void genesisTest() {
		/*
		Blockchain bc = new Blockchain();
		System.out.println("--------------<>-----------" + Block.GENESIS_BLOCK);
		bc.addBlock(Block.GENESIS_BLOCK);
		System.out.println("--------------<>-----------");

		// assertEquals(Block.GENESIS_BLOCK, bc.getHighestBlock());
		RSA carl = new RSA();
		int blocks = 10;
		Block nxt = new Block(null, carl.getPublic(), 1);

		nxt.setPrevious(bc.getHighestBlock().getBlockHash());
		nxt.setTimestamp(System.currentTimeMillis());
		nxt.hash();
		System.out.println("--------------<>-----------");

		System.out.println("--------------<>-----------");
		System.out.println("--------------<>-----------");
		System.out.println(nxt);
		System.out.flush();


		bc.addBlock(nxt);


		Long bal = blocks*Block.BLOCK_REWARD;
		// assertEquals(bal, bc.queryBalance(carl.getPublic()));
		*/

	}
	// @Test
	public void spendTest() {
		// time to spend money!!
		Blockchain bc = new Blockchain();
		bc.addBlock(Block.GENESIS_BLOCK);
		System.out.println(Block.GENESIS_BLOCK);
		assertEquals(Block.GENESIS_BLOCK, bc.getHighestBlock());

		RSA carl = new RSA();
		RSA fred = new RSA();
		int blocks = 10;
		for (int i=0;i<blocks;i++) {
			Transaction[] tx = new Transaction[1];
			tx[0] = new Transaction(fred.getPublic(),carl,10);
			Block nxt = new Block(i > 0 ? tx : null, carl.getPublic(), i+1);
			nxt.setPrevious(bc.getHighestBlock().getBlockHash());
			nxt.setTimestamp(System.currentTimeMillis());
			nxt.hash();

			bc.addBlock(nxt);
			try {
				// bc otherwise the timestamp would lineup LOL
				Thread.sleep(69);
			} catch (Exception e) {
				continue;
			}
			// bc.printBalances();

		}

		// Long bal = blocks*Block.BLOCK_REWARD - (blocks-1L) * 10L;
		// assertEquals(bal, bc.queryBalance(carl.getPublic()));
		// Long bal2 = 10L*(blocks-1);
		// assertEquals(bal2, bc.queryBalance(fred.getPublic()));
	}

	public void branchTest() {
		// test going to another branch, and then switching

	}

}