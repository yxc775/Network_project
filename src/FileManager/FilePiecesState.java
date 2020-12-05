package FileManager;

import Config.CommonAttributes;
import MessageObjects.Piece;

public class FilePiecesState {
    public Piece[] filePiecesList;
    public int size;

    public FilePiecesState() {
        size = (int) Math.ceil(((double) CommonAttributes.filesize / (double) CommonAttributes.piecesize));
        this.filePiecesList = new Piece[size];

        for (int i = 0; i < this.size; i++)
            this.filePiecesList[i] = new Piece();
    }

    public static FilePiecesState convertBitFieldToFileInfo(byte[] payload) {
        FilePiecesState toReturn = new FilePiecesState();
        for(int i = 0 ; i < payload.length; i ++)
        {
            int count = 7;
            while(count >=0)
            {
                int test = 1 << count;
                if(i * 8 + (8-count-1) < toReturn.size)
                {
                    if((payload[i] & (test)) != 0)
                        toReturn.filePiecesList[i * 8 + (8-count-1)].haveit = true;
                    else
                        toReturn.filePiecesList[i * 8 + (8-count-1)].haveit = false;
                }
                count--;
            }
        }

        return toReturn;
    }

    public synchronized boolean checkAnyFileListMismatch(FilePiecesState otherSideFilesState) {
        int cursize = otherSideFilesState.size;


        //Return true if the sender side has the file we need
        for (int i = 0; i < cursize; i++) {
            if (otherSideFilesState.filePiecesList[i].haveit
                    && !this.filePiecesList[i].haveit) {
                return true;
            }
        }

        return false;
    }

    public synchronized int findFirstFileListMisMatch(FilePiecesState theirState) {
        int maxIndex = Math.min(this.size, theirState.size);
        for (int i = 0; i < maxIndex; i++) {
            if (theirState.filePiecesList[i].haveit
                    && !this.filePiecesList[i].haveit) {
                return i;
            }
        }
        return -1;
    }

    public byte[] getBitFieldByteArray() {
        int s = this.size / 8;
        if (size % 8 != 0)
            s = s + 1;
        byte[] iP = new byte[s];
        int tempInt = 0;
        int count = 0;
        int Cnt;
        for (Cnt = 1; Cnt <= this.size; Cnt++)
        {
            boolean haveit = this.filePiecesList[Cnt-1].haveit;
            tempInt = tempInt << 1;
            if (haveit)
            {
                tempInt = tempInt + 1;
            } else
                tempInt = tempInt + 0;

            if (Cnt % 8 == 0 && Cnt!=0) {
                iP[count] = (byte) tempInt;
                count++;
                tempInt = 0;
            }

        }
        if ((Cnt-1) % 8 != 0)
        {
            int tempShift = ((size) - (size / 8) * 8);
            tempInt = tempInt << (8 - tempShift);
            iP[count] = (byte) tempInt;
        }
        return iP;
    }


    public void checkOwndedBitField(String pid, boolean hasFile) {
        if (hasFile) {
            for (Piece x : filePiecesList) {
                x.haveit = true;
                x.owner = pid;
            }
        } else {
            for (Piece x : filePiecesList) {
                x.haveit = false;
                x.owner = pid;
            }
        }
    }

    public int countHavedPieces()
    {
        int count = 0;
        for (int i = 0; i < this.size; i++)
            if (this.filePiecesList[i].haveit)
                count++;

        return count;
    }

    public boolean hasALLPieces(){
        for(Piece x: filePiecesList){
            if(!x.haveit){
                return false;
            }
        }
        return true;
    }
}
