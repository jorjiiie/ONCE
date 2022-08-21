package ONCE.data;

import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;

// interface for double ended data stream (rw)
// might be better as an abstract class
public interface DataStream {
	
	// public readObject readObject();
	// public void writeObject(SerialObject obj);
	// for now
	public Object readObject();
	public void writeObject(Serializable obj);
	public void setInputStream(InputStream in);
	public void setOutputStream(OutputStream out);
}