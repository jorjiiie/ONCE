package ONCE.data;

import ONCE.core.*;

import java.io.Serializable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BuiltinObjectStream implements DataStream {
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public BuiltinObjectStream() {
		
	}

	public BuiltinObjectStream(InputStream inS, OutputStream outS) {
		setInputStream(inS);
		setOutputStream(outS);
	}

	public void setInputStream(InputStream inS) {
		try {

			in = new ObjectInputStream(inS);

		} catch (Exception e) {

			e.printStackTrace();
			Logging.log("ERROR: DataInputStream was not properly created");

		}
	}
	public void setOutputStream(OutputStream outS) {
		try {

			out = new ObjectOutputStream(outS);

		} catch (Exception e) {

			e.printStackTrace();
			Logging.log("ERROR: DataOutputStream was not properly created");

		}
	}
	public Object readObject() {
		assert in != null;

		Object o = null;

		try {

			if (in.available() == 0) {
				Logging.log("DataStream is blocking and waiting for input, please notice!!!");
			}
			o = in.readObject();
			Logging.log("done reading");

		} catch (Exception e) {

			e.printStackTrace();
			Logging.log("ERROR: DataStream could not read object properly");

		}
		return o;
	}
	public void writeObject(Serializable obj) {

		assert out != null;

		try {

			out.writeObject(obj);

		} catch (Exception e) {

			e.printStackTrace();
			Logging.log("ERROR: Datastream could not write object properly");

		}
	}
}