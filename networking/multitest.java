import java.net.*;
import java.io.*;

public class multitest {
	public static void main(String[] args) {
		int cnt = 1;
		try (
		ServerSocket server = new ServerSocket(8069);
		) {
			System.out.println(server.getLocalPort());
			System.out.println(InetAddress.getLocalHost().getHostAddress());

			while (true) {
				Socket soc = server.accept();
				TestThread thread = new TestThread(soc, cnt++);
				thread.start();
				System.out.println(" new connection");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

