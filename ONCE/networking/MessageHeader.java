package ONCE.networking;

import java.io.Serializable;

// instead of serializing everything we can customize everything? and have like in the protocol readType and it'll do stuff necessary
// that way we can control for the version updates
// do I care
// it would be much much much better for future usability to do that but we aren't doing that anyways
// BUT THAT IS A BAD ATTITUDE bc if you're gonna do it you may as well go all the way
// we will do this a poc but move over pretty quickly, I don't think it will interferere with anything and I'll just have to change the protocol and some communication stuffs
// we will have ONCEProtocol protocol = new Protocol_0_0_1() or something and override the functions
// biggest issue is that it is not language independent
public class MessageHeader extends Message implements Serializable {
	public final int TYPE;
	// don't know if this is necessary, since it may be able to just pick up the blocks on its own
	public final int SIZE;
	public final String CHECKSUM;

	public MessageHeader(int type, int sz, String checksum) {
		super(1);
		TYPE = type;
		SIZE = sz;
		CHECKSUM = checksum;
	}

}