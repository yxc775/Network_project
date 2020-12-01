package MessageObjects;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import Utility.Util;

//这里是将原来的handshakemessage和messageconstants合并在一起写了
public class HandShake {
    private static final int PEERID_LENGTH = 4;
    private static final int HANDSHAKE_FULLLENGTH = 32;
    public static final String correctHeader = "P2PFILESHARINGPROJ";
    public static final byte[] correctZerobits = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    private byte[] header = new byte[18];
    private byte[] zeroBits = new byte[10];
    private byte[] peerID = new byte[4];

    public HandShake(String header,byte[] zeroBits,int peerId) {
        try {
            this.header = header.getBytes(StandardCharsets.UTF_8);
            this.peerID = Util.convertInttoFourByte(peerId);
            this.zeroBits = zeroBits;
            checkIntegrity();
        } catch (Exception e) {
            Util.PrintLog(e.toString());
        }
    }

    //decode byteMessage
    public static HandShake decodeMessage(byte[] receivedMessage) {
        HandShake handshakeMessage = null;
        byte[] header = new byte[correctHeader.length()];
        byte[] zeroBits = new byte[correctZerobits.length];
        byte[] peerID = new byte[PEERID_LENGTH];
        try {
            if (Util.convertByteToString(receivedMessage).length() != HANDSHAKE_FULLLENGTH) {
                throw new Exception("HandShake Message HEADER Mismatching");
            }

            // Decode the received message
            System.arraycopy(receivedMessage, 0, header,
                    0, correctHeader.length());
            System.arraycopy(receivedMessage, correctHeader.length(),
                    zeroBits, 0, correctZerobits.length);
            System.arraycopy(receivedMessage, correctHeader.length() + correctZerobits.length,
                    peerID,
                    0, PEERID_LENGTH);

            handshakeMessage = new HandShake(Util.convertByteToString(header),zeroBits,Util.convertByteToInt(peerID));
            return handshakeMessage;
        } catch (Exception e) {
            Util.PrintLog(e.toString());
            return null;
        }
    }

    //Check integrity of the handshake message
    public void checkIntegrity() {
        try {
            if (!(new String(this.header, StandardCharsets.UTF_8).equals(correctHeader)))
                throw new Exception("Header mismatch.");
            if (this.peerID.length > PEERID_LENGTH)
                throw new Exception("Peer ID is too long.");
            if (!Arrays.equals(this.zeroBits, correctZerobits))
                throw new Exception("Zero bits mismatch.");
        } catch (Exception e) {
            Util.PrintLog("Handshake datagram mismatch, integrity checking fail " + e.toString());
        }
    }

    public byte[] getHeader() {

        return header;
    }

    public byte[] getZeroBits() {
        return zeroBits;
    }

    public byte[] getPeerID() {
        return peerID;
    }


    public String toString() {
        return ("[HandshakeMessage] : Peer Id - " + this.peerID.toString()
                + ", Header - " + this.header.toString());
    }


    //Convert byteMessage content to HandShake object
    public byte[] encodeMessage() {
        byte[] sendMessage = new byte[HANDSHAKE_FULLLENGTH];
        //Copy header byte
        System.arraycopy(this.getHeader(), 0, sendMessage,
                0, this.getHeader().length);

        //Copy zerodigit byte
        System.arraycopy(this.getZeroBits(), 0,
                sendMessage, this.getHeader().length,
                this.getZeroBits().length);
        //Copy PeerId byte
        System.arraycopy(this.getPeerID(), 0, sendMessage,
                this.getHeader().length + this.getZeroBits().length,
                this.getPeerID().length);

        return sendMessage;
    }
}