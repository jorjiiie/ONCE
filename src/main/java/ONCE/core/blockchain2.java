package ONCE.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

import java.math.BigInteger;


/**
 * @author: jorjiiie
 * August 3 2022
 * Blockchain class that handles *exclusively* blockchain operations -> add and retrieve (delete is not supported because its an invalid operation)
 * Does *NOT* handle balances, valid transactions, etc 
 */


public class blockchain2 {

    private TreeMap<String, Integer> branches;
    // tbh dont worry about this for now lmao
    // this shouldnt get serialized btw
    // private BlockCache cache;
    // private 
    /**
     * Constructor
     * Initializes everything
     */
    public blockchain2() {
        // cache = new BlockCache();

    }

    public void addBlock(Block b) {
        // add block
    }

    public Block query(String s) {
        // look for the file
        /*
        try(
            ) {

        } catch (Exception e) {
            e.printStackTrace();
            Logging.log("Error querying for block " + s);
        }
        */
        return null;
    }
    public Block query(Block b) {
        return query(b.getBlockHash());
    }
}