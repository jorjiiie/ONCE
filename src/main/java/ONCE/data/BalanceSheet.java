package ONCE.data;

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
 */
public class BalanceSheet implements Serializable {
	private String branchHash;
	private String junctionHash;
	private HashMap<String, BigInteger> balances;
	public BalanceSheet(String branch) {
		branchHash = branch;

	}

	public void setPreviousSheet(String junction) {
		junctionHash = junction;
	}
}