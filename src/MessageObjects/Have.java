package MessageObjects;
import Utility.Util;

public class Have extends Message {
    public int pieceIndex;
    public static int INT_BYTE_SIZE = 4;
    public Have(byte[] messagePayload) {
        super((byte) 4,INT_BYTE_SIZE + 1);
        pieceIndex = Util.convertByteToInt(messagePayload);
    }
}