package MessageObjects;

public class Interested extends Message {
    public Interested() {
        super((byte) 2,1);
        super.hasPayload = false;
    }
}
