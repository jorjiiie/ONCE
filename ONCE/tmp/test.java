package ONCE.tmp;

import java.sql.Timestamp;


import ONCE.core.*;


public class test {
	public static void main(String[] args) {
		Timestamp a = new Timestamp(System.currentTimeMillis());
		System.out.println(a);	
		Logging.log("Hello!");
	}
}
