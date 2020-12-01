package FileManager;

import Config.CommonAttributes;
import MessageObjects.Piece;

public class FilePiecesManager {
    public Piece[] filePiecesList;
    public int size;
    public FilePiecesManager(){
        size = (int) Math.ceil(((double) CommonAttributes.filesize/ (double) CommonAttributes.piecesize));
        this.filePiecesList = new Piece[size];

        for (int i = 0; i < this.size; i++)
            this.filePiecesList[i] = new Piece();
    }

    public byte[] getBitFieldByteArray()
    {
        int s = this.size / 8;
        //add one extra space to handle extra file piece in residual of 8
        if (size % 8 != 0) {
            s = s + 1;
        }
        byte[] bitfield = new byte[s];
        int boolList = 0;
        int i;
        int j = 0;
        //loop through all available piece and output the correlated BitField

        for (i = 1; i <= this.size; i++)
        {
            boolean haveThePiece = this.filePiecesList[i-1].haveit;
            //need to use the boollist to mark whether the piece is present from high to low bit
            boolList = (boolList << 1) + (haveThePiece ? 1 : 0);

            if (i+1 % 8 == 0) {
                bitfield[j] = (byte) boolList;
                j++;
                //reset the boolist to 0 since we just fill a byte unit
                boolList = 0;
            }
        }

        //Some file piece bit field may be ignored if only handling the 8 times entity.
        if ((i-1) % 8 != 0)
        {
            int tempShift = ((size) - (size / 8) * 8);
            boolList = boolList << (8 - tempShift);
            bitfield[j] = (byte) boolList;
        }
        return bitfield;
    }





    public void checkOwndedBitField(String pid, boolean hasFile){
        if(hasFile){
            for(Piece x : filePiecesList){
                x.haveit = true;
                x.ownder = pid;
            }
        }
        else{
            for(Piece x : filePiecesList){
                x.haveit = false;
                x.ownder = pid;
            }
        }
    }
}
