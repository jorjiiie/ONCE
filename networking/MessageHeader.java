import java.net.*;
import java.io.*;
// this is incredibly lazy but lol!
public class MessageHeader implements Serializable {
	public final int type;
	public MessageHeader(int n) {
		type = n;
	}
}