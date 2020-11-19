package MessageObjects;

import Config.CommonAttributes;

//this is the payload for message
public class BitField extends Message {
    public Boolean[] piecesInfo;
    public int BIT_COUNT = 8;
    public int pieceNum;
    public BitField(byte[] messagePayload) {
        super((byte) 5,(int) Math.ceil(((double) CommonAttributes.filesize / (double) CommonAttributes.piecesize)) + 1);
        super.hasPayload = true;
        pieceNum = super.messageLen - 1;
        this.piecesInfo = new Boolean[pieceNum];

        for(int i = 0 ; i < messagePayload.length; i ++)
        {
            for(int j = BIT_COUNT - 1; j >= 0; j--){
                int shift = 1 << j;
                if(i * BIT_COUNT - (BIT_COUNT - i - 1) < pieceNum){
                    //True indicates that piece this peer already have, false indicates that piece this peer does not have
                    if((messagePayload[i] & shift) != 0){
                        piecesInfo[i * BIT_COUNT + (BIT_COUNT - j - 1)] = true;
                    }
                    else{
                        piecesInfo[i * BIT_COUNT + (BIT_COUNT - j - 1)] = false;
                    }
                }
            }
        }

    }
    public Boolean[] getPiecesInfo(){
        return piecesInfo;
    }

    public byte[] encode(BitField bitfield){
        //TODO:need to implement encode
        return new byte[1];
    }

    public String toString(){
        return super.toString()
                + ", Data - "
                + this.piecesInfo.toString();
    }
}
