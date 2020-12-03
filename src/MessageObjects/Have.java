package MessageObjects;
import Utility.Util;

public class Have implements Message {
    public byte[] messageLength;
    public byte messagetype;
    public int pieceIndex;
    public static int INT_BYTE_SIZE = 4;
    public byte[]  payload;
    public Have(byte[] messagePayload) {
        this.messagetype = (byte) 4;
        this.messageLength = Util.convertInttoFourByte(INT_BYTE_SIZE + 1);
        pieceIndex = Util.convertByteToInt(messagePayload);
        payload = messagePayload;
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