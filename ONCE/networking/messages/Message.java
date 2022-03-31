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

/**
 * Generic message type that handles a header and data
 * @author jorjiiie
 */

// in future plans to make language agnostic, I think it's still ok but header should include a size in bytes
public class Message implements Serializable {

    final MessageHeader header;
    final Object data;

    /**
     * Constructor
     * @param _header MessageHeader that has the data common to all messages
     * @param _data payload of the message, null if message is a rejection or a no-payload message
     */
    public Message(MessageHeader _header, Object _data) {
        header = _header;
        data = _data;
    }
}