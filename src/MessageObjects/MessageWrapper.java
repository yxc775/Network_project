package MessageObjects;

import Utility.Util;

public class MessageWrapper {
    public Message messageClassObject;
    public final static int CHOKE_TYPE = 0;
    public final static int UNCHOKE_TYPE = 1;
    public final static int INTERESTED_TYPE = 2;
    public final static int NOTINTERESTED_TYPE = 3;
    public final static int HAVE_TYPE = 4;
    public final static int BITFIELD_TYPE = 5;
    public final static int REQUEST_TYPE = 6;
    public final static int PIECE_TYPE = 7;

    public static final int DATA_MSG_LEN = 4;
    public static final int DATA_MSG_TYPE = 1;
    public static byte[] payload;
    public int fromSenderPeer;
    public static Integer type;
    public MessageWrapper(int messageType, byte[] payload, int fromSenderPeer){
        this.type = messageType;
        this.payload = payload;
        this.messageClassObject = wrapToClassObject(messageType,payload);
        this.fromSenderPeer = fromSenderPeer;
    }


    public byte[] encode()
    {
        return messageClassObject.encode();
    }


    public Message wrapToClassObject(int type, byte[] payload){
        try {
            switch (type) {
                case CHOKE_TYPE:
                    return new Choke();
                case UNCHOKE_TYPE:
                    return new UnChoke();
                case INTERESTED_TYPE:
                    return new Interested();
                case NOTINTERESTED_TYPE:
                    return new NotInterested();
                case HAVE_TYPE:
                    return new Have(payload);
                case BITFIELD_TYPE:
                    return new BitField(payload);
                case REQUEST_TYPE:
                    return new Request(payload);
                case PIECE_TYPE:
                    return new Piece(payload);
                default:
                    throw new Exception("invalid message type");
            }
        }
        catch(Exception e){
            Util.PrintLog("Unknown Message Type detected During wrapping!!");
            return null;
        }
    }
}

