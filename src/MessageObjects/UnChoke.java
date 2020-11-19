package MessageObjects;

public class UnChoke extends Message {
    public UnChoke() {
        super((byte) 1,1);
        super.hasPayload = false;
    }

}