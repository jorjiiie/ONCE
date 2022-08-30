package ONCE.data;

// interface for serializing in/out
// potentially turn into abstract class with header/data like messages

// MAYBE use json for human readability?!?!?! that seems to be a very important distinction


// this is defined recursively!
//	{
//		magic:
//		data: {
//			int: 50203,
//			string: "wow",
//			array: n,
//			magic:
//			data: {
//				stuff for each thing
//			}
//			magic: ....
//			(n times)
//			data: {
//				stuff for each thing
//			}
//
//		}
//	}



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