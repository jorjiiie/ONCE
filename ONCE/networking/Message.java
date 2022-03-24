package ONCE.networking;

/*
 * Message base class that we can use to check for message types
 * This is a protorype version and should (and will hopefully) be removed when I am less lazy
 * 0 - reject and go back to neutral state
 * 1 - message header
 * 2 - block
 * 3 - transaction
 * 4 - ip
 * 5 - request?
 * 6 - connect message
 */

import java.io.Serializable;
public class Message implements Serializable {
	public final int type;
	public Message(int n) {
		type = n;
	}
}