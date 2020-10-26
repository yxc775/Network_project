package Message;

import Config.CommonAttributes;

//this is the payload for message
public class BitField extends Message {
    public Piece[] pieces;
    public BitField() {
        super((byte) 5,(int) Math.ceil(((double) CommonAttributes.filesize / (double) CommonAttributes.piecesize)) + 1);
        this.pieces = new Piece[super.messageLen - 1];
    }
}
