import java.util.HashMap;



// run the threads in client?
public class Client
{



	// n threads for mining
	// 2 threads (A and B) for transaction listening and block listening
	// i don't think there's anything else 
	// stuff could be paralellized but lets be honest it's not going to make a difference

	// C(N) = n thread mine HASHES_PER_CYCLE times
	// concurrently A thread checks for new blocks and B thread checks for new transactions & compiles them in multiples of 2 into new merkletree
	// C(N) are done they all check thread A and B if any new things have happened 
		// if A has something, then they take the new block and toss the transactions that are already existing
		// if B has something if the new merkletree is done then they take it, otherwise they go back to mining

	// if A has something, communicates with B to toss the existing transactions that are in the block


	/* MiningThread
		Block currentBlock
		SharedData flag
			just has a flag for A and a flag for B
	*/

	/* BlockThread
		Block currentBlock
		Block newBlock
		SharedData flag
			flag to turn on new block

	*/

	/* TransactionThread
		Block currentBlock
		Transaction newTransactions[TRANSACTION_MAX]
		MerkleTree merkle
		SharedData flag
			flag for new transactions
		
	*/
	public boolean confirm_transaction(Transaction t) {
		// check through chain if the transaction is valid
		// meaning check if it already exists, or the transaction number is wrong
		return false;
	}
	public boolean add_transaction(Transaction t) {
		return true;
	}




	public static void main(String[] args) {


	
		int numCores = Runtime.getRuntime().availableProcessors();

		SharedData sd = new SharedData("hi");

		// init a random block for us to use

		Block b = new Block(0, null, null);


		MinerThread[] miners = new MinerThread[numCores];
		BlockListenerThread block_listener = new BlockListenerThread(sd, "block listener", b);
		for (int i=0;i<numCores;i++) {
			miners[i] = new MinerThread(sd ,"Thread "+i, b);
			miners[i].start();
		}


		// no class initialization for client
		// just run client

		// check for init files here
		
		// todo:
		// valid mining rewards
		// mining rewards
		// valid blocks
		/*
		User user = new User();
		user.initUser();
		User test = new User();
		test.initUser();
		User god = new User();
		god.initUser();

		int n = 5;
		Transaction t[] = new Transaction[n];
		for (int i=0;i<n;i++) {
			t[i] = god.sendTo(user.getPublicKey(),5);
			// System.out.println(t[i]);
		}

		Block b = new Block(0, t, null);

		b.startHash(15);

		HashMap<byte[], Block> chain = new HashMap<byte[], Block>();
		
		chain.put(b.getHash(),b);


		byte[] previousBlock = b.getHash();

		for (int i=0;i<10;i++) {
			t = new Transaction[n];
			
			for (int j=0;j<n;j++) {
				t[j] = god.sendTo(user.getPublicKey(),(int)(Math.random() * 6+1)); 
			}
			Block currentBlock = new Block(chain.get(previousBlock).getHeight()+1, t, previousBlock);

			do {
				currentBlock.startHash(15);
			} while (chain.containsKey(currentBlock.getHash()));

			chain.put(currentBlock.getHash(),currentBlock);

			previousBlock = currentBlock.getHash();
		}

		while (previousBlock != null) {
			System.out.println(HashUtils.byteToHex(previousBlock));
			previousBlock = chain.get(previousBlock).get_previous();
		}
		// for (Block bb : chain.values()) {
		// 	System.out.println(bb);
		// }
		// */
	}
}
