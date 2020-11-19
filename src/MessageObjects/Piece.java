package MessageObjects;

import Util.Util;

public class Piece extends Message {
    private byte[] byteIndex;
    private byte[] filePart;
    public int INDEX_LENGTH = 4;
    public int index;
    public boolean haveit = false;
    public String ownder;

    public Piece(int size){
        filePart = new byte[size];
        index = -1;
        haveit = false;
        ownder = null;
    }

    public Piece(byte[] messagePayload)
    {
        super((byte) 7,1+messagePayload.length);
        byteIndex = new byte[INDEX_LENGTH];
        System.arraycopy(messagePayload, 0, byteIndex, 0, INDEX_LENGTH);
        index = Util.convertByteToInt(byteIndex);
        filePart = new byte[messagePayload.length - INDEX_LENGTH];
        System.arraycopy(messagePayload, INDEX_LENGTH, filePart, 0, messagePayload.length-INDEX_LENGTH);
    }
}
