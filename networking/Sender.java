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