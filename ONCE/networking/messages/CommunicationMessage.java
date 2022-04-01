package ONCE.networking.messages;

import ONCE.core.HashUtils;

import java.io.Serializable;

public class CommunicationMessage implements Serializable, Payload {
	public final String message;
	public final String author;
	// can add a signature here so that we can accept/propogate messages from a specific node

	public CommunicationMessage(String msg, String auth) {
		message = msg;
		author = auth;
	}
	public String checksum() {
		return HashUtils.sHash(message+author);
	}
}