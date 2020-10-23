package Message;

public abstract class Message {
    private byte messageType = 0;
    public int messageLen = 0;

    public Message(byte messageType, int messageLen) {
        this.messageType = messageType;
        this.messageLen = messageLen;
    }

    public int getMessageLength() {
        return messageLen;
    }


    public byte getMessageType() {
        return messageType;
    }

}
