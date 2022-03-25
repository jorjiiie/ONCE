public class CommunicationMessage extends MessageHeader {
	String message, author;
	public CommunicationMessage(String str, String a) {
		super(3);
		message = str;
		author = a;
	}
}