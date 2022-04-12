package ONCE.mining;

public class HashReturn {
	public final long salt;
	public final boolean success;

	public HashReturn(long salt, boolean success) {
		this.salt = salt;
		this.success = success;
	}
}