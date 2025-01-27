package MessageObjects;

import Config.CommonAttributes;
import Utility.Util;

public class Piece implements Message {
    private byte[] byteIndex;
    private byte[] filePart;
    public static int INDEX_LENGTH = 4;
    public int index;
    public boolean haveit = false;
    public String owner;
    public byte messageType;
    public byte[] messageLength;
    public byte[] payload;
    public int fromPeer;

    public Piece(){
        filePart = new byte[CommonAttributes.piecesize];
        index = -1;
        haveit = false;
        owner = null;
    }

    public Piece(byte[] messagePayload)
    {
        messageType = (byte)7;
        messageLength = Util.convertInttoFourByte(1 + messagePayload.length);
        payload = messagePayload;
        byteIndex = new byte[INDEX_LENGTH];
        System.arraycopy(messagePayload, 0, byteIndex, 0, INDEX_LENGTH);
        index = Util.convertByteToInt(byteIndex);
        filePart = new byte[messagePayload.length - INDEX_LENGTH];
        System.arraycopy(messagePayload, INDEX_LENGTH, filePart, 0, messagePayload.length-INDEX_LENGTH);
    }

    public byte[] getFilePart(){
        return this.filePart;
    }

    public void setHaveIt(){
        this.haveit = true;
    }

    public void setFromPeer(int peerId){
        this.fromPeer = peerId;
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

    @Override
    public byte[] encode() {
        return Util.encodeMessageWithPayload(this);
    }
}
