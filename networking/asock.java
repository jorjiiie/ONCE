import java.io.*;
import java.net.*;

public class asock {
	public static void main(String[] args) {
		
		try(
			Socket soc = new Socket("localhost", 8069);
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		) {
			out.println("aaaa");
			String cin = in.readLine();
			System.out.println(cin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}