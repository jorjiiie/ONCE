package ONCE.data;

import ONCE.core.*;

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
 * 
 * this kind of does have a fat storage footprint if we make it persistent bc to branch at any one point we need to keep track of each of the things anyways
 * essentially trading storage for the performance (probably performance is a better hit to take but it doesn't rlly matter lol we can just switch it to not be accumulative)
 */
public class BalanceSheet implements Serializable {
	private String branchHash;
	private String junctionHash;
	// technically should be coinimplementation but whatever
	private HashMap<String, BigInteger> balances;
	public BalanceSheet(String branch) {
		branchHash = branch;
	}
	public BalanceSheet(String branch, Block block) {
		// put the blocks transactions into it
		Transaction[] transactions = block.getTransactions();
		for (Transaction tx : transactions) {
			// balances.compute(tx.getSender(), (k,v) -> (v==null) ? -tx.getAmount() : v - tx.getAmount());
			// balances.compute(tx.getReciever(), (k,v) -> (v==null) ? +tx.getAmount() : v + tx.getAmount());
		}
	}
	public String getJunctionSheet() {
		return junctionHash;
	}
	public void setJunctionSheet(String junction) {
		junctionHash = junction;
	}

	public void extendPreviousSheet(BalanceSheet previous) {
		previous.balances.forEach((k, v) -> balances.merge(k, v, BigInteger::add));
	}
}