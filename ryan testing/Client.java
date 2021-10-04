import java.util.HashMap;


public class Client
{

	public boolean confirm_transaction(Transaction t) {
		// check through chain if the transaction is valid
		// meaning check if it already exists, or the transaction number is wrong
		return false;
	}
	public boolean add_transaction(Transaction t) {
		return true;
	}


	public static void main(String[] args) {
		// no class initialization for client
		// just run client

		// check for init files here
		
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
	}
}
