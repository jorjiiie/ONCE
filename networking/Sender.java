import java.net.*;
import java.io.*;

public class Sender {
    public static void test(Object o) {
        System.out.println(o);
    }
    public static void main(String[] args) {
        System.out.println("???");
        try (
            Socket soc = new Socket("localhost", 8069);
            ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
            // essentially, for main program you can keep a bunch of these in the broadcasters, and only activate on requests
            // listeners are always in their thread chillin
            // do not close!! right now we are closing with errors LOL
            // we may not ever need to close by error since we can just
            // go like broadcasterA -> listenerB, listenerB dead -> broadcasterA dead
            // broadcasterA dead -> listenerA dead
        ) {
            BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
            String inS = "";
            MessageHeader sendApple = new MessageHeader(2);
            MessageHeader sendBlock = new MessageHeader(1);
            Apple[] f = new Apple[20];
            for (int i=0;i<20;i++) {
                f[i] = new Apple();
            }
            f[0] = new Apple("joe");
            Block b = new Block("Carl", 50, f);

            while (!inS.equals("bye")) {
                inS = cin.readLine();
                System.out.println(inS);   
                if (inS.toLowerCase().equals("apple")) {
                    // send the apple
                    APPLEProtocol prot = new APPLEProtocol(false);
                    prot.receiver(soc, sendApple, f[0], in, out);
                } else if (inS.toLowerCase().equals("block")) {
                    APPLEProtocol prot = new APPLEProtocol(false);
                    prot.receiver(soc, sendBlock, b, in, out);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}