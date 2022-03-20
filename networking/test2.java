import java.net.*;
import java.io.*;

public class test2 {
	public static void main(String[] args) {
		try(
			ServerSocket server = new ServerSocket(8069);
			Socket client = server.accept();
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			

		) {
			try {
				Object obj = in.readObject();
				if (obj instanceof Block) {
					System.out.println("it is a block");
				} 
				if (obj instanceof Apple) {
					System.out.println("apple");
				} 
				System.out.println(obj);
				Block b = (Block) obj;
				System.out.println("received " + b);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("oopsie");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}