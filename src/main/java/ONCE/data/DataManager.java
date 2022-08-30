package ONCE.data;

/**
 * @author: jorjiiie
 * date: august 21 2022
 * datamanger :sunglasses
 */


import ONCE.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystem;

import java.io.Serializable;

// public interface to use data manging - this automatically handles caching + reading off disk
public class DataManager<K extends Comparable<? super K>, V extends Serializable> {

	private static final String DATA_PATH = System.getProperty("user.dir") + "/ONCE_DATA/";
	public static final String DEFAULT_EXTENSION = ".once";


	private Cache<K,V> cache = new Cache<>(1);

	// program should check if its the only file in the directory? - on startup! !!!
	private final String path;
	private final String extension;
	public DataManager() {
		path = DATA_PATH + "trash/";
		File f = new File(path);
		f.mkdirs();
		// debugging 
		File[] files = f.listFiles();
		for (File file : files) {
			// potentially delete everything
			System.out.println(file);
			file.delete();
		}
		extension = DEFAULT_EXTENSION;
	}

	public DataManager(String s) {
		path = DATA_PATH+s+ "/";
		new File(path).mkdirs();
		extension = DEFAULT_EXTENSION;
	}

	public String getPath() {
		return path;
	}
	// caller MUST check that its correct form so maybe a wrapper for that but idk how to do that so we might have to deal w a lot of
	// boiler plate code :(
	// for ex. 
	// DataManager<String, Block> blockdb
	// Object res = blockdb.query(hash )
	// if (res instanceof Block) (or rather assert res instanceof Block)
	//		Block block = (Block) res
	//	... do something
	//	else 
	// ...	throw error or whatever you want to do
	public Object query(K key) {
		Object res = cache.query(key);
		if (res != null) {
			Logging.log("Cache hit for " + key.toString());
			return res;
		}
		// find it in the thing

		File data_file = new File(path+key.toString()+extension);

		if (data_file.exists() == false) 
			return null;
		

		try {

			DataStream fileIn = new BuiltinObjectStream();
			fileIn.setInputStream(new FileInputStream(data_file));
			res = fileIn.readObject();

		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("wtf");
		}

		if (res != null)
			cache.insert(key, (V) res);
		return res;

	}
	// should only be called when checked for first but im going to check anyways since idk what im doing
	// returns true if added sucessfully 
	public boolean add(K key, V value) {

		// this can fuck up if you recieve in succession - solution is to wait a few seconds before rebroadcasting
		// should lock this tbh but whatever
		Object obj = query(key);
		if (obj != null) {
			return false;
		}

		cache.insert(key, value);

		File data_file = new File(path+key.toString()+extension);

		try {

			DataStream fileOut = new BuiltinObjectStream();
			fileOut.setOutputStream(new FileOutputStream(data_file));
			fileOut.writeObject(value);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error writing value " + value + " to file " + data_file);
		}
		return true;
	}

	public static void main(String[] args) {
		// this can be an input argument or smth (probably not but the init file)
		DataManager<String, String> stringdb = new DataManager<>();

		System.out.println(stringdb.getPath());

		System.out.println(stringdb.query("joe"));

		System.out.println(stringdb.add("joe", "joe\'s key is very cool"));

		System.out.println(stringdb.add("123","1"));

		System.out.println(stringdb.query("123"));

		System.out.println(stringdb.query("joe"));


	}
}