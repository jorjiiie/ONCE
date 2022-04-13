package ONCE.mining;

public class HashReturn {
	public final long salt;
	public final boolean success;
	public final long timestamp;

	public HashReturn(long salt, long timestamp, boolean success) {
		this.salt = salt;
		this.success = success;
		this.timestamp = timestamp;
	}
}