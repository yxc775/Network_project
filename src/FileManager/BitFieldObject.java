package FileManager;

import Config.CommonAttributes;
import MessageObjects.Piece;

public class BitFieldObject {
    public Piece[] objects;
    public int size;
    public BitFieldObject(){
        size = (int) Math.ceil(((double) CommonAttributes.filesize/ (double) CommonAttributes.piecesize));
        this.objects = new Piece[size];

        for (int i = 0; i < this.size; i++)
            this.objects[i] = new Piece(size);
    }

    public void checkOwndedBitField(String pid, boolean hasFile){
        if(hasFile){
            for(Piece x : objects){
                x.haveit = true;
                x.ownder = pid;
            }
        }
        else{
            for(Piece x : objects){
                x.haveit = false;
                x.ownder = pid;
            }
        }
    }
}
