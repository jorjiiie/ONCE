package ONCE.core;

import java.io.Serializable;
public class CoinImplementation implements Serializable {

	public static final CoinImplementation ZERO = new CoinImplementation(0L);
	private Long coins;

	public CoinImplementation(Long n) {
		coins = n;
	}

	public CoinImplementation addCoins(CoinImplementation other) {
		// to switch to bigint just do that equivalent coins.add(other.coins)
		return new CoinImplementation(coins + other.coins);
	}
	public boolean greaterThan(CoinImplementation other) {
		return coins > other.coins;
	}
	public boolean greaterThanOrEquals(CoinImplementation other) {
		return coins >= other.coins;
	}
	public boolean lessThan(CoinImplementation other) {
		return coins < other.coins;
	}
	public boolean lessThanOrEquals(CoinImplementation other) {
		return coins <= other.coins;
	}
	public String toString() {
		return coins.toString();
	}
	public CoinImplementation negate() {
		return new CoinImplementation(-coins);
	}


	public boolean equals(Object other) {
		if (this == null || other == null)
			return false;
		if (!(other instanceof CoinImplementation))
			return false;
		return coins.equals(((CoinImplementation) other).coins);
	}

}