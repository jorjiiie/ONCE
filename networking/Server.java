import java.net.*;
import java.io.*;

public class Server {
	public static void main(String[] args) {
		System.out.println("hi");
		try (
			ServerSocket server = new ServerSocket(8069);
			Socket client = server.accept();
		) {
			Logging.log("connected");
			System.out.println(client.getInetAddress());
			System.out.println(client.getLocalAddress());
			System.out.println(client.getPort());
			System.out.println(client.getLocalPort());

			APPLEProtocol prot =new APPLEProtocol(true);
			// new Thread() {
			// 	public void run() {
					
			// 		// shut down lmfao!
			// 		try {
			// 			Thread.sleep(10000);
			// 			client.shutdownInput();
			// 			client.shutdownOutput();
			// 			client.close();
			// 		} catch (Exception e) {
			// 			e.printStackTrace();
			// 		}
			// 	}
			// }.start();

			// we can interrupt then send a thing before closing??
			prot.start(client);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}