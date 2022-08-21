package ONCE.data;

// interface for serializing in/out
// potentially turn into abstract class with header/data like messages

// Header of each object:
//
public interface SerialObject {
	
	public byte[] writeObject();
	public SerialObject readObject(byte[] data);

	public byte[] serializeHeader();
	public byte[] serializeData();
	default byte[] serializeObject() {
		// i this this is unnecessary and should just be included in serializeobject
		return null;
	}
}