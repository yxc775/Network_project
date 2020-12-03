package MessageObjects;

import Utility.Util;

public class Interested implements Message {
    byte messageType;
    byte[] messageLength;
    public Interested() {
        this.messageType = (byte)2;
        this.messageLength = Util.convertInttoFourByte(1);
    }

    @Override
    public byte[] getMessageLength() {
        return messageLength;
    }

    @Override
    public byte getMessageType() {
        return messageType;
    }

    @Override
    public byte[] getPayload() {
        return null;
    }

    @Override
    public boolean hasPayload() {
        return false;
    }

    @Override
    public byte[] encode() {
        return Util.encodeMessageWithOutPayload(this);
    }


}
