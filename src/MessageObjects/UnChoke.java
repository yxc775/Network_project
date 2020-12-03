package MessageObjects;

import Utility.Util;

public class UnChoke implements Message {
    byte messageType;
    byte[] messageLength;
    public UnChoke() {
        this.messageType = (byte)1;
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