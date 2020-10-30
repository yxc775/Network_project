package HandShake;

import Peer.RemotePeerInfo;
import java.io.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import Peer.peerProcess;

//这里是将原来的handshakemessage和messageconstants合并在一起写了
public class HandShake {

    // Attributes
        private byte[] header = new byte[18];
        private byte[] zeroBits = new byte[10];
        private byte[] peerID = new byte[4];
        private String messageHeader = "P2PFILESHARINGPROJ";
        private String messagePeerID;

        /* Class constructor
         *
         * Header Handshake header string
         * PeerId Peer ID
         *
         */
        public HandShake() {

        }

        public HandShake(String Header, String PeerId) {

            try {
                this.messageHeader = Header;
                this.header = messageHeader.getBytes(StandardCharsets.UTF_8);
                if (this.header.length > 18)
                    throw new Exception("Header is too long.");

                this.messagePeerID = PeerId;
                this.peerID = messagePeerID.getBytes(StandardCharsets.UTF_8);
                if (this.peerID.length > 4)
                    throw new Exception("Peer ID is too long.");

                this.zeroBits = "0000000000".getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                peerProcess.PringLog(e.toString());
            }

        }


    // Set the handShakeHeader
    public void setHeader(byte[] handShakeHeader) {
        this.messageHeader = (new String(handShakeHeader, StandardCharsets.UTF_8)).toString().trim();
        this.header = this.messageHeader.getBytes();
    }


    // Set the peerID
    public void setPeerID(byte[] peerID) {
        this.messagePeerID = (new String(peerID, StandardCharsets.UTF_8)).toString().trim();
        this.peerID = this.messagePeerID.getBytes();

    }

        // return the handShakeHeader
        public byte[] getHeader() {
            return header;
        }

        // return the peerID
        public byte[] getPeerID() {
            return peerID;
        }

        // Set the zeroBits
        public void setZeroBits(byte[] zeroBits) {
            this.zeroBits = zeroBits;
        }

        // return the zeroBits
        public byte[] getZeroBits() {
            return zeroBits;
        }

        // return the messageHeader
        public String getHeaderString() {
            return messageHeader;
        }

        // return the messagePeerID
        public String getPeerIDString() {
            return messagePeerID;
        }

        // Return the toString method of the Object
        public String toString() {
            return ("[HandshakeMessage] : Peer Id - " + this.messagePeerID
                    + ", Header - " + this.messageHeader);
        }

        // Decodes the byte array HandshakeMessage and loads to the object HandshakeMessage
        public static HandShake decodeMessage(byte[] receivedMessage) {

            HandShake handshakeMessage;
            byte[] msgHeader;
            byte[] msgPeerID;

            try {
                // Initial check
                if (receivedMessage.length != 32)
                    throw new Exception("Byte array length not matching.");

                // VAR initialization
                handshakeMessage = new HandShake();
                msgHeader = new byte[18];
                msgPeerID = new byte[4];

                // Decode the received message
                System.arraycopy(receivedMessage, 0, msgHeader, 0,
                        18);
                System.arraycopy(receivedMessage, 18
                                + 10, msgPeerID, 0,
                        4);

                // Populate handshakeMessage entity
                handshakeMessage.setHeader(msgHeader);
                handshakeMessage.setPeerID(msgPeerID);

            } catch (Exception e) {
                peerProcess.PringLog(e.toString());
                handshakeMessage = null;
            }
            return handshakeMessage;
        }

        // Encodes a given message in the format HandshakeMessage
        public static byte[] encodeMessage(HandShake handshakeMessage) {

            byte[] sendMessage = new byte[32];

            try {
                // Encode header
                if (handshakeMessage.getHeader() == null) {
                    throw new Exception("Invalid Header.");
                }
                if (handshakeMessage.getHeader().length <= 18 && handshakeMessage.getHeader().length != 0) {
                    System.arraycopy(handshakeMessage.getHeader(), 0, sendMessage,
                            0, handshakeMessage.getHeader().length);
                } else {
                    throw new Exception("Invalid Header.");
                }

                // Encode zero bits
                if ((handshakeMessage.getZeroBits() == null) ||
                    (handshakeMessage.getZeroBits().length > 10 || handshakeMessage.getZeroBits().length == 0)){
                    throw new Exception("Invalid zero bits field.");
                }
                else {
                    System.arraycopy(handshakeMessage.getZeroBits(), 0,
                            sendMessage, 18,
                            10 - 1);
                }

                // Encode peer id
                if ((handshakeMessage.getPeerID() == null) ||
                   (handshakeMessage.getPeerID().length > 4 || handshakeMessage.getPeerID().length == 0) ){
                    throw new Exception("Invalid peer id.");
                } else {
                    System.arraycopy(handshakeMessage.getPeerID(), 0, sendMessage,
                            18 + 10,
                            handshakeMessage.getPeerID().length);
                }

            } catch (Exception e) {
                peerProcess.PringLog(e.toString());
                sendMessage = null;
            }

            return sendMessage;

        }
}
