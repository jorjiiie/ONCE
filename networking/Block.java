import java.io.Serializable;

import java.util.ArrayList;
public class Block extends MessageHeader implements Serializable {
	private String hash;
	private int cnt;
	private Apple[] fruits;

	public Block() {
		super(1);
		hash = "";
		cnt = 10;
		fruits = null;
	}
	public Block(String h, int c, Apple[] f) {
		super(1);
		hash = h;
		cnt = c;
		fruits = f;
	}
	public String toString() {
		String ret = "" + hash + " " + cnt + " [" ;
		for (int i=0;i<fruits.length-1;i++) {
			ret += fruits[i].toString() + ", ";
		}
		ret += fruits[fruits.length-1] + "]";
		return ret;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		final Block b = (Block) obj;
		if (!hash.equals(b.hash) || cnt != b.cnt || fruits.length != b.fruits.length) {
			return false;
		}
		for (int i = 0; i < fruits.length; i++) {
			if (!fruits[i].equals(b.fruits[i])) {
				return false;
			}
		}
		return true;
	}
}