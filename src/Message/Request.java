package Message;

public class Request extends Message {
    public int pieceIndex;
    public static int INT_BYTE_SIZE = 4;
    public Request(int messagePayload) {
        super((byte) 6, INT_BYTE_SIZE + 1);
        pieceIndex = messagePayload;
    }

}
