import java.net.*;
import java.io.*;

public class test {
	public static void main(String[] args) throws java.io.IOException {
		ServerSocket server = new ServerSocket(8069);
		System.out.println(server.getLocalPort());
		//System.out.println(server.getLocalSocketAddress());
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		Socket client = server.accept();
		BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		String aa = bf.readLine();
		out.println("joe\n");
		System.out.println(aa);
		        System.out.println(InetAddress.getLocalHost());

		server.close();
		Socket socket = new Socket();
socket.connect(new InetSocketAddress("google.com", 80));
System.out.println(socket.getLocalAddress());
socket.close();
	}
}
