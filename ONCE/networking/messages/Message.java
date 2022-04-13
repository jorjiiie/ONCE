package ONCE.networking.messages;


import java.io.Serializable;

/**
 * Generic message type that handles a header and data
 * @author jorjiiie
 */

public class Message implements Serializable {

    public final MessageHeader header;
    public final Payload data;

    /**
     * Constructor
     * @param _header MessageHeader that has the data common to all messages
     * @param _data payload of the message, null if message is a rejection or a no-payload message
     */
    public Message(MessageHeader _header, Payload _data) {
        header = _header;
        data = _data;
    }
    public String toString() {
        // 
        return "lol";
    }

    /**
     * Verify that the contents of the messare are what they were supposed to be
     */
    public boolean verify() {
        return (header.checksum.equals(data.checksum()));
    }
}