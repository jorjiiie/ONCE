import java.net.*;
import java.io.*;

public class multitest {
	public static void main(String[] args) throws InterruptedException {
		int cnt = 1;
		try (
		ServerSocket server = new ServerSocket(8069);
		) {
			System.out.println(server.getLocalPort());
			System.out.println(InetAddress.getLocalHost().getHostAddress());

			while (true) {
				Socket soc = server.accept();
				TestThread thread = new TestThread(soc, cnt++);
				System.out.println(soc);
				thread.start();
				System.out.println(" new connection");
				System.out.println(""+soc.getReceiveBufferSize() + " " + soc.getSendBufferSize());
				// thread.interrupt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

