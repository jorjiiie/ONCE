import java.net.*;
import java.io.*;


public class TestThread extends Thread {
	private Socket client;
	private int id;
	public TestThread(Socket _client, int _id) {
		super("joe");
		client = _client;
		id = _id;
	}
	public void run() {
		// wait for inputs and process those??
		try (
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		) {
			String inp = "";
			out.println("connected to server\n");
			while (!inp.equals("Bye")) {
				inp = in.readLine();
				System.out.println("Sent (" + inp + ") to connection " + id);
				out.println("Server: " + inp);
				System.out.println("Recieved (" + inp + ") from connection " + id);	
			}
			System.out.println("Connection " + id + " closed");
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
