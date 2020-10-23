package Message;

public class Piece extends Message {
    public Piece(byte[] messagePayload) {
        super((byte) 7,1 );
    }
}
