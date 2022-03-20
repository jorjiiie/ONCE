import java.net.*;
import java.io.*;

// this is not a protocol lmao its just the handling
// could use an abstract class here so it do good later when you have "updates"
public class APPLEProtocol {
    private int STATE = 0;
    private boolean host = false;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public APPLEProtocol(boolean b) {
        host = b;
    }
    public void receiver(Socket host, MessageHeader header, Object data, ObjectInputStream _in, ObjectOutputStream _out) {
        // handle once it gets a request

        in = _in;
        out = _out;
        try {
            out.writeObject(header);
            System.out.println("Sent to server");
            Object confirmation = in.readObject();
            System.out.println("Recieved Confirmation: " + confirmation);
            switch(header.type) {
                case 1:
                    // the block mf
                    Block b = (Block) data;
                    out.writeObject(b);
                    break;
                case 2:
                    Apple a = (Apple) data;
                    out.writeObject(a);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("communication failed");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void start(Socket client) {

        try (
            ObjectOutputStream _out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream _in = new ObjectInputStream(client.getInputStream());
        ) { 
            in = _in;
            out = _out;
            Apple confirmation = new Apple("donda estas");
            while (!client.isInputShutdown()) {
                /*
                Object obj = in.readObject();
                if (obj instanceof RejectionMessage) {
                    // ALL messages should extend Message which has type, then we can always check the type so we can handle rejection, close, and others easily w/o making a whole new ass class for it
                    STATE = 0;
                    continue;
                }
                */
                System.out.println("READING: " + client.getInputStream().available());

                Object obj;
                try {
                    obj = in.readObject();
                } catch (EOFException e) {
                    e.printStackTrace();
                    System.out.println("wtf");
                    break;
                }

                System.out.println(obj);
                switch(STATE) {
                    case 0:
                        try {
                            MessageHeader m = (MessageHeader) obj;
                            if (m.type == 1) 
                                STATE = 2;
                            if (m.type == 2) 
                                STATE = 3;
                            out.writeObject(confirmation);

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("something happened, try sending a new request" + STATE);
                            STATE = 0;
                        }

                        break;
                    case 2:
                        try {
                            Block b = (Block) obj;

                            System.out.println("RECIEVED A NICE BLOCK: " + b);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("???");
                        }
                        STATE = 0;
                        break;
                    case 3:
                        try {
                            Apple a = (Apple) obj;
                            System.out.println("recieved apple uwu: " + a);

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("jasld");
                        }
                        STATE = 0;
                        break;

                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}