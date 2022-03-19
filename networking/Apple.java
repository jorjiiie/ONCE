import java.io.Serializable;

public class Apple implements Serializable {
	private static int cnt = 0;
	private String name;

	public Apple() {
		name = "apple #" + ++cnt;
	}
	public Apple(String n) {
		name = n;
	}
	public String toString() {
		return name;
	}
}