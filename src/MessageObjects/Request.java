package MessageObjects;
import Utility.Util;

public class Request implements Message {
    public int pieceIndex;
    public byte messageType;
    public byte[] messageLength;
    public static int INT_BYTE_SIZE = 4;
    public byte[] payload;
    public Request(byte[] messagePayload) {
        this.messageType = (byte)6;
        this.messageLength = Util.convertInttoFourByte(1 + INT_BYTE_SIZE);
        this.payload = messagePayload;
        pieceIndex = Util.convertByteToInt(messagePayload);
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
        return payload;
    }

    @Override
    public boolean hasPayload() {
        return true;
    }
}
