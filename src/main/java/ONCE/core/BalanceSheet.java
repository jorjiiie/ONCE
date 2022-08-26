package ONCE.core;

import java.math.BigInteger;

import java.util.HashMap;

import java.io.Serializable;

/**
 * @author: jorjiiie
 * date: August 3 2022
 * Balance Sheet class - keeps track of changes over a "long" period of time. One balance sheet is created for every branch,
 * and on verification of block, the program will jump from balance sheet to balance sheet, which accumulates balances until a branching event, until the genesis is reached
 * Worse case O(n) where n is length of the longest blockchain, but this would require a FULLY 'saturated' blockchain where every single node is branched, which is highly unlikely
 * Better guess = near constant since branching events are uncommon, however, the "low" level branches will end up occupying a lot of computational power due to the necessary check (even if the branch is 1 block long)
 * so if the blockchain grows to large scales it'll get screwed (not awful but just kind of unnecessary)
 * at a branch the old branch skips straight to the previous junction but the new branch will require a manufacturing of a new junction and has to jump from (any branch n > 1) branch -> new junction -> previous junction and the first branch is always first branch -> old junction
 * I do wonder what is the difference between doing this and just making a new balance sheet for each branch from the genesis - perhaps this makes it easier to have "persistence" and less variation bc once a sheet is down it should never be changed
 */
public class BalanceSheet implements Serializable {
	private String branchHash;
	private String junctionHash;
	// technically should be coinimplementation but whatever
	private HashMap<BigInteger, CoinImplementation> balances = new HashMap<>();
	public BalanceSheet(String branch) {
		branchHash = branch;
	}
	public BalanceSheet(Block block) {
		// put the blocks transactions into it
		branchHash = block.getBlockHash();
		Transaction[] transactions = block.getTransactions();
		for (Transaction tx : transactions) {
			balances.compute(tx.getSender(), (k,v) -> (v==null) ? tx.getAmount().negate() : v.addCoins(tx.getAmount().negate()));
			balances.compute(tx.getReciever(), (k,v) -> (v==null) ? tx.getAmount() : v.addCoins(tx.getAmount()));
		}
	}
	public String getJunctionSheet() {
		return junctionHash;
	}
	public void setJunctionSheet(String junction) {
		junctionHash = junction;
	}

	public void extendPreviousSheet(BalanceSheet previous) {
		if (previous.balances != null)
			previous.balances.forEach((k, v) -> balances.merge(k, v, CoinImplementation::addCoins));
	}
	public CoinImplementation getChange(BigInteger addr) {
		return balances.getOrDefault(addr, CoinImplementation.ZERO);
	}
}