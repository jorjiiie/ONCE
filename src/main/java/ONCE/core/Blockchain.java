package ONCE.core;

import ONCE.data.DataManager;

import java.util.TreeMap;

import java.math.BigInteger;


/**
 * @author: jorjiiie
 * August 3 2022
 * Blockchain class that handles *exclusively* blockchain operations -> add and retrieve (delete is not supported because its an invalid operation)
 * Does *NOT* handle balances, valid transactions, etc 
 */


public class Blockchain {

    private BlockRecord currentLongestBranch = null;
    // so having this like this means that it will always read the block AND the balance sheet off which is not necessarily optimal, we only need the balance sheet (id say 100% speedup available here)
    private DataManager<String, BlockRecord> database;

    // private 
    /**
     * Constructor
     * Initializes everything
     */
    public Blockchain() {
        database = new DataManager<>();
    }
    public Blockchain(String network) {
        // make this change the port btw
        database = new DataManager<>(network);
    }

    public boolean addBlock(Block b) {
        // add block
        BlockRecord existingRecord = queryBlock(b.getBlockHash());
        if (existingRecord != null) {
            // check for longest branch - everything before should be in place
            if (currentLongestBranch == null || existingRecord.block.getDepth() > currentLongestBranch.block.getDepth()) {
                currentLongestBranch = existingRecord;
            }
            return false;
        }

        BlockRecord rec = new BlockRecord(b);
        database.add(b.getBlockHash(), rec);

        BlockRecord previousRec = queryBlock(b.getPrevious());
        previousRec.addChildren();
        if (previousRec.getChildren() > 1) {
            // this is the new junction
            rec.sheet.setJunctionSheet(b.getPrevious());
        } else {
            rec.sheet.setJunctionSheet(previousRec.sheet.getJunctionSheet());
            System.out.println(previousRec.sheet + " " + rec.sheet);
            rec.sheet.extendPreviousSheet(previousRec.sheet);
        }

        if (currentLongestBranch == null || b.getDepth() > currentLongestBranch.block.getDepth()) {
            currentLongestBranch = rec;
        }
        return true;
    }

    public BlockRecord queryBlock(String s) {
        Object block = database.query(s);
        if (block == null)
            return (BlockRecord) block;
        try {
            BlockRecord rec = (BlockRecord) block;
            return rec;

        } catch (Exception e) {
            e.printStackTrace();
            Logging.log("Failed to return block record from db (casting error or something)");
        }
        return null;

    }
    public BlockRecord queryBlock(Block b) {
        return queryBlock(b.getBlockHash());
    }
    // shouhld abstract to address
    public CoinImplementation queryBalance(BigInteger addr) {

        CoinImplementation balance = CoinImplementation.ZERO;

        String currentBlock = currentLongestBranch.block.getBlockHash();
        BlockRecord currentRecord = (BlockRecord) database.query(currentBlock);
        while (currentBlock != null) {
            currentRecord = queryBlock(currentBlock);
            BalanceSheet currentSheet= currentRecord.sheet;
            CoinImplementation money = currentSheet.getChange(addr);

            balance = balance.addCoins(money);

            currentBlock = currentSheet.getJunctionSheet();
        }
        return balance;
    }

    public boolean verifyTransaction(Transaction tx) {    
        if (tx.verify() == false)
            return false; 

        // transaction is valid, look for balance

        CoinImplementation balance = queryBalance(tx.getSender());
        if (balance.greaterThan(tx.getAmount()))
            return true;
        else
            return false;
    }

    public Block getHighestBlock() {
        return currentLongestBranch.block;
    }
}