package MessageObjects;

public interface Message {
    public byte[] getMessageLength();
    public byte getMessageType();
    public byte[] getPayload();
    public boolean hasPayload();
}
