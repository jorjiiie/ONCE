import java.net.*;
import java.io.*;
public class AppleSender {
	public static void main(String[] args) {
		try (
			Socket soc = new Socket("localhost", Integer.parseInt(args[0]));
			ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
		) {
			Apple[] f = new Apple[20];
			for (int i=0;i<20;i++) {
				f[i] = new Apple();
			}
			f[0] = new Apple("joe");
			Block b = new Block("Carl", 50, f);
			out.writeObject(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}