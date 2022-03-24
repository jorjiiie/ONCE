package ONCE.networking;

// could turn this abstract but i am making a mock up first
public class ONCEProtocol {
	private static final int WAITING = 1;
	private static final int SEND_BLOCK = 5;
	private static final int SEND_TX = 15;

	private final int VERSION;
	private int state;
	private boolean host;
	private Socket soc;
	// if you put a shareddata bc host yknow maybe??

	// do not think there should be a version here lol
	public ONCEProtocol(boolean _host, int version) {
		state = WAITING;
		host = _host;
		VERSION = version;
		state = 0;
	}
	private Message readMessage(ObjectInputStream in) {
		try {
			Object o = in.readObject();
			if ()
		} catch (Exception e) {
			e.printStackTrace();
			Logging.log("Error when reading from "  + soc);
		}
	}
	public void processHost(Socket client) {
		soc = client;
		try (
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        ) { 
        	Message init = readMessage();
            while (!client.isInputShutdown()) {
            	Object o = in.readObject();
            	Message m = (Message) o;
            	switch (m.type) {
	            	case 0:
	            		break;
            		case 1:
            			break;
            		case 2:
            			break;
            		case 3:
            			break;
            		case 4:
            			break;
            		case 5:
            			break;
            		case 6:
            			break;
            		case 7:
            			break;

            	}
            }
    	}
	}
}