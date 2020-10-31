package HandShake;

import java.nio.charset.StandardCharsets;

import Peer.peerProcess;
import Util.Util;

//这里是将原来的handshakemessage和messageconstants合并在一起写了
public class HandShake {
    private int PEERID_LENGTH = 4;
    private int HANDSHAKE_FULLLENGTH = 32;
    private static String correctHeader = "P2PFILESHARINGPROJ";
    private static String correctZerobits = "0000000000";

    private byte[] header;
    private byte[] zeroBits;
    private byte[] peerID;

    public HandShake(String header, String zerobits, String peerId) {
        try {
            this.header = header.getBytes(StandardCharsets.UTF_8);
            this.peerID = peerId.getBytes(StandardCharsets.UTF_8);
            this.zeroBits = zerobits.getBytes(StandardCharsets.UTF_8);
            checkIntegrity();
        } catch (Exception e) {
            peerProcess.PrintLog(e.toString());
        }
    }

    //decode byteMessage
    public HandShake(byte[] receivedMessage) {
        HandShake handshakeMessage = null;
        byte[] header = new byte[correctHeader.length()];
        byte[] zeroBits = new byte[correctZerobits.length()];
        byte[] peerID = new byte[PEERID_LENGTH];
        try {
            if (Util.convertByteToString(receivedMessage).length() != HANDSHAKE_FULLLENGTH) {
                throw new Exception("HandShake Message HEADER Mismatching");
            }

            // Decode the received message
            System.arraycopy(receivedMessage, 0, header,
                    0, correctHeader.length());
            System.arraycopy(receivedMessage, correctHeader.length(),
                    zeroBits, 0, correctZerobits.length());
            System.arraycopy(receivedMessage, correctHeader.length() + correctZerobits.length(),
                    peerID,
                    0, PEERID_LENGTH);
            this.header = header;
            this.zeroBits = zeroBits;
            this.peerID = peerID;
            checkIntegrity();
        } catch (Exception e) {
            peerProcess.PrintLog(e.toString());
        }

    }

    //Check integrity of the handshake message
    public void checkIntegrity() {
        try {
            if (!(new String(this.header, StandardCharsets.UTF_8).equals(correctHeader)))
                throw new Exception("Header mismatch.");
            if (this.peerID.length > PEERID_LENGTH)
                throw new Exception("Peer ID is too long.");
            if (!(new String(this.zeroBits, StandardCharsets.UTF_8).equals(correctZerobits)))
                throw new Exception("Zero bits mismatch.");
        } catch (Exception e) {
            peerProcess.PrintLog(e.toString());
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

    //Convert handshakeMessage to byte objects
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
