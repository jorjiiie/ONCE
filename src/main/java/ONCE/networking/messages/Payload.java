package ONCE.networking.messages;

/**
 * Interface for a payload that has a method for grabbing checksums
 */
public interface Payload {
	public String checksum();
}