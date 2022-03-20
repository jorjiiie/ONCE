import java.net.*;
import java.io.*;

public class Server {
	public static void main(String[] args) {
		System.out.println("hi");
		try (
			ServerSocket server = new ServerSocket(8069);
			Socket client = server.accept();
		) {
			System.out.println("connected");
			APPLEProtocol prot =new APPLEProtocol(true);
			prot.start(client);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}