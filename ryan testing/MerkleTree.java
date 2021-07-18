import java.util.List;
import java.util.ArrayList;

public class MerkleTree
{
	private int leaf_count, node_count;
	// array-tree representation where chilren of node i are 2i and 2i+1
	// root is nodes[1]
	private Node[] nodes;


	public MerkleTree(ArrayList<Transaction> leafs)
	{
		ArrayList<byte[]> hashes = new ArrayList<byte[]>(leafs.size());
		for (int i=0;i<leafs.size();i++)
		{
			hashes.set(i,leafs.get(i).getHash());
		}
		buildTree(hashes);
	}
	public void buildTree(ArrayList<byte[]> leafHash)
	{
		// constructs tree with leafHash array
		// make sure its even
		leaf_count = leafHash.size() | 1;
		nodes = new Node[leaf_count * 2];
		for (int i=leaf_count+1; i<leaf_count*2;i++) nodes[i] = new Node(leafHash.get(i));
		for (int i=leaf_count;i>0;i--)
		{
			nodes[i] = new Node(nodes[i*2], nodes[i*2+1]);
		}	
	}
	public String toString()
	{
		String ret  = "";
		ret += "size: " + node_count + "\nLeaf Count: " + leaf_count + "\n";
		// put nodes togehter somehow	
		for (int i=1;i<=node_count;i++)
		{
			ret += "Node " + i + ": " + nodes[i] + "\n";
		}
		return ret;
	}

	static class Node
	{
		public boolean isLeaf;
		public byte[] hash;
		public Node left,right;

		public Node(byte[] hash)
		{
			this.hash = hash;
			isLeaf = true;

		}
		public Node(Node l, Node r)
		{
			left = l;
			right = r;
			// combine hashes
			byte[] combinedHash = new byte[64];
			for (int i=0;i<32;i++) combinedHash[i] = l.hash[i];
			for (int i=32;i<64;i++) combinedHash[i] = r.hash[i];
			this.hash = HashUtils.hash(combinedHash);
		}
		public String toString()
		{
			return "Leaf: " + isLeaf + HashUtils.byteToHex(hash);
		}		
	}

	public static void main(String[] args)
	{
		String joe = "hi";

		byte[] jj = HashUtils.hash(joe);

		System.out.printf("%s hashed to %s",joe,HashUtils.byteToHex(jj));
	}





}