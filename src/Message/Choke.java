package Message;

public class Choke extends Message {
    public Choke(byte[] messageLen) {
        super((byte) 0,1);
    }

}
