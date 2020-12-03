package MessageObjects;

import Utility.Util;

public class BitField implements Message {
    byte[] payload;
    byte[] messageLength;
    byte messagetype;


    public BitField(byte[] payload){
        this.messagetype = (byte)5;
        this.messageLength = Util.convertInttoFourByte(1 + payload.length);
        this.payload = payload;
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
        return payload;
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    @Override
    public byte[] encode(){
        return Util.encodeMessageWithPayload(this);
    }


}
