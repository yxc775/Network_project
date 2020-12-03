package MessageObjects;

import Utility.Util;

public class NotInterested implements Message {
    byte messageType;
    byte[] messageLength;
    public NotInterested() {
        this.messageType = (byte)3;
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
