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

    //create a package which will be sent through socket
    public byte[] encode(){
        byte[] msg = new byte[messageLength.length + 1 + payload.length];
        byte[] messageTypeWrapper = new byte[1];
        messageTypeWrapper[0] = this.messagetype;

        System.arraycopy(getMessageLength(), 0, msg, 0, getMessageLength().length);
        System.arraycopy(messageTypeWrapper, 0, msg, getMessageLength().length, messageTypeWrapper.length);
        System.arraycopy(getPayload(), 0, msg, getMessageLength().length + messageTypeWrapper.length, getPayload().length);

        return msg;
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


}
