package MessageObjects;

import Utility.Util;

public class Choke implements Message {
    byte[] messageLength;
    byte messagetype;
    public Choke() {
        this.messagetype = (byte)0;
        this.messageLength = Util.convertInttoFourByte(1);
    }

    @Override
    public byte[] getMessageLength() {
        return messageLength;
    }

    @Override
    public byte getMessageType() {
        return messagetype;
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
