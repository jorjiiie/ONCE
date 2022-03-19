import java.net.*;
import java.io.*;
public class sock {
	public static void main(String[] args) throws java.io.IOException {

		try(
			Socket soc = new Socket("localhost", 8069);
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		) {
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			String inS = "", outS;

			inS = in.readLine();
			System.out.println("init: " + inS);
			//idk why i need this but ok
			inS = in.readLine();
			while (!soc.isInputShutdown() && !inS.equals("Bye")) {
				
				outS = cin.readLine();
				out.println(outS);
				inS = in.readLine();
				System.out.println("[Client sent] : " + outS);
				System.out.println("[Server sent]: " + inS );
				if (outS.equals("Bye")) {
					break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

