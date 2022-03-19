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
	@Override
	public void interrupt() {
		try {
			client.shutdownInput();
			client.shutdownOutput();
			client.close();
			System.out.println("closed");
		} catch(Exception e) {
			System.out.println("oh no");
		}
	}
	public void run() {
		// wait for inputs and process those??
		try (
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		) {
			String inp = "";
			out.println("connected to server\n");
			while (!client.isClosed() && !inp.equals("Bye")) {
				inp = in.readLine();
				System.out.println("Sent (" + inp + ") to connection " + id);
				out.println("Server: " + inp);
				System.out.println("Recieved (" + inp + ") from connection " + id);	
				// Thread.sleep(5000);
			}
			System.out.println("Connection " + id + " closed");
		}  catch(java.net.SocketException e) {
			// this is just it gets closed by the thread
			System.out.println("Connection " + id + " closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
