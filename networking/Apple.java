import java.io.Serializable;

public class Apple extends MessageHeader implements Serializable {
	private static int cnt = 0;
	private String name;

	public Apple() {
		super(2);
		name = "apple #" + ++cnt;
	}
	public Apple(String n) {
		super(2);
		name = n;
	}
	public String toString() {
		return name;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		final Apple apple = (Apple) obj;
		return name.equals(apple.name);
	}
}