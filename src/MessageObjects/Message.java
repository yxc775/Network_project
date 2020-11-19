package MessageObjects;

public abstract class Message {
    private byte messageType = 0;
    public int messageLen = 0;
    public boolean hasPayload;

    public Message(){

    }
    public Message(byte messageType, int messageLen) {
        this.messageType = messageType;
        this.messageLen = messageLen;
    }

    public String toString(){
        return "[Message]: Message Length - "
                + this.messageLen
                + ", Message Type - "
                + this.messageType;
    }

    public int getMessageLength() {
        return messageLen;
    }


    public byte getMessageType() {
        return messageType;
    }

}
