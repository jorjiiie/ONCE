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

    // needs to save a properties (last highest, version, etc)

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
        System.out.println("TRYING TO ADD BLOCK " + b.getBlockHash());
        System.out.flush();
        BlockRecord existingRecord = queryBlock(b.getBlockHash());
        if (existingRecord != null) {
            Logging.log("Query hit: " + b.getBlockHash());
            // check for longest branch - everything before should be in place
            if (currentLongestBranch == null || existingRecord.block.getDepth() > currentLongestBranch.block.getDepth()) {
                currentLongestBranch = existingRecord;
            }
            System.out.println("Found block already");
            return false;
        }

        BlockRecord rec = new BlockRecord(b);

        if (b.getDepth() > 1) {
            BlockRecord previousRec = queryBlock(b.getPrevious());

            previousRec.addChildren();
            if (previousRec.getChildren() > 1) {
                // this is the new junction
                rec.sheet.setJunctionSheet(b.getPrevious());
            } else {
                rec.sheet.setJunctionSheet(previousRec.sheet.getJunctionSheet());
                rec.sheet.extendPreviousSheet(previousRec.sheet);
                System.out.println(previousRec.sheet + "extending to:\n" + rec.sheet);

            }
        } else {
            rec.sheet.setJunctionSheet(Block.GENESIS_HASH);
            System.out.println(rec.sheet);
        }
        database.add(b.getBlockHash(), rec);

        if (currentLongestBranch == null || b.getDepth() > currentLongestBranch.block.getDepth()) {
            currentLongestBranch = rec;
        }
        return true;
    }

    public BlockRecord queryBlock(String s) {
        Object block = database.query(s);
        if (block != null)
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

        BlockRecord currentRecord = currentLongestBranch;
        while (currentRecord != null) {
            BalanceSheet currentSheet= currentRecord.sheet;
            CoinImplementation money = currentSheet.getChange(addr);

            System.out.println("CURRENT SHEET !&@*(#&!@(#&!(@#(!*@#");
            System.out.println(currentSheet);

            balance = balance.addCoins(money);

            currentRecord = queryBlock(currentSheet.getJunctionSheet());

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

    public void traverseBlockChain() {
        // start from highest block;
        BlockRecord cur = currentLongestBranch;
        while (cur != null) {
            Logging.log(cur.block + "\n---------" +cur.sheet);
            cur = (BlockRecord) database.query(cur.block.getPrevious());
        }
    }
    public static void main(String[] args) {
        Blockchain bc = new Blockchain();
        System.out.println(Block.GENESIS_BLOCK);
        bc.addBlock(Block.GENESIS_BLOCK);

        RSA carl = new RSA();
        Block nb = new Block(null, carl.getPublic(), 1);
        nb.setPrevious(bc.getHighestBlock().getBlockHash());
        nb.setTimestamp(0);
        nb.hash();
        System.out.println(nb);

        bc.addBlock(nb);


        Block nb2 = new Block(null, carl.getPublic(), 2);
        nb2.setPrevious(bc.getHighestBlock().getBlockHash());
        nb2.setTimestamp(1);
        nb2.hash();
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("AJSKLDJASLKDJAKLSDJLASD DONE WAITING **********************");
        System.out.println(nb2);
        bc.addBlock(nb2);

        Block nb3 = new Block(null, carl.getPublic(), 2);
        nb3.setPrevious(nb.getBlockHash());
        nb3.setTimestamp(1);
        nb3.hash();

        // System.out.println("done hashing");
        System.out.println(nb3);
        bc.addBlock(nb3);

        System.out.println(bc.getHighestBlock());

        System.out.println("QUERY BALANCE: " + bc.queryBalance(carl.getPublic()));


        bc.traverseBlockChain();
    }
}