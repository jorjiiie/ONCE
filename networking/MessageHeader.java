import java.net.*;
import java.io.*;
// this is incredibly lazy but lol!
public class MessageHeader implements Serializable {
	public final int type;
	// 1 - block
	// 2 - apple
	// 0 - connect message
	public MessageHeader(int n) {
		type = n;
	}
}