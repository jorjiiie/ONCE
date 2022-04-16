package ONCE.networking.messages;

import ONCE.core.HashUtils;
import ONCE.core.Transaction;
import java.io.Serializable;

// should (definitely able) be able to make this multiple transactions but i dont really care that much
public class TransactionMessage implements Serializable, Payload {
	public final Transaction tx;

	public TransactionMessage(Transaction tx) {
		this.tx = tx;
	}
	public String checksum() {
		return tx.getHash();
	}
}