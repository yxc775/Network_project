package Peer;


import MessageObjects.*;
import Utility.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//this will be running as a thread, for receive data from other process
public class RemoteHandler implements Runnable{
    private Socket socket = null;
    private peerProcess curProcess;
    private HandShake handshakeMsg;
    private int MESSAGE_LENTH_BYTE = 4;
    private int MESSAGE_TYPE_BYTE = 1;

    private InputStream input;
    private OutputStream output;
    private boolean isSender;
    private int curPeerid;
    private int desPeerid;
    //
    public RemoteHandler(peerProcess curProcess, Socket remoteSocket, boolean isSender, int curPeerid){
        this.curProcess = curProcess;
        this.socket = remoteSocket;
        this.isSender = isSender;
        this.curPeerid = curPeerid;
        try{
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch (IOException e){
            Util.PrintLog(this.curPeerid + " get error during making connection" + e.getMessage());
        }
    }

    public RemoteHandler(peerProcess curProcess,int portnum, boolean isSender, String address){
        this.curProcess = curProcess;
        this.curPeerid = curProcess.remotePeerInfo.peerId;
        this.isSender = isSender;
            try{
                this.socket = new Socket(address,portnum);
            }
            catch(IOException e){
                e.printStackTrace();
                System.exit(0);
            }

            try{
                input = socket.getInputStream();
                output = socket.getOutputStream();
            }
            catch(Exception ex){
                Util.PrintLog(curProcess.remotePeerInfo.peerId + " error: " + ex.getMessage());
                System.exit(0);
            }
    }

    public boolean sendHandShake(){
        try{
            output.write(new HandShake(HandShake.correctHeader,curPeerid).encodeMessage());
            return true;
        }
        catch(IOException e){
            Util.PrintLog(curPeerid + " SendHandshake: " + e.getMessage());
            return false;
        }
    }
    public void run(){
        byte[] handshakeBuffer = new byte[32];
        byte[] messageHeader = new byte[MESSAGE_LENTH_BYTE + MESSAGE_TYPE_BYTE];
        try{
            if(this.isSender){
                if(!sendHandShake()){
                    Util.PrintLog(this.curPeerid + " HANDSHAKE sending failed.");
                }
                else{
                    Util.PrintLog(this.curPeerid + " HANDSHAKE has been send.");
                }
                boolean waitforHandshakeRespond = true;
                while(waitforHandshakeRespond){
                    input.read(handshakeBuffer);
                    handshakeMsg = HandShake.decodeMessage(handshakeBuffer);
                    if(Util.convertByteToString(handshakeMsg.getHeader()).equals(HandShake.correctHeader)){
                        desPeerid = Util.convertByteToInt(handshakeMsg.getPeerID());
                        Util.PrintLog(curPeerid + " makes a connection to Peer " + desPeerid);
                        Util.PrintLog(curPeerid + " Received a HANDSHAKE message from Peer  " + desPeerid);
                        ProcessManager.despeerIdToSocket.put(desPeerid, this.socket);
                        waitforHandshakeRespond = false;
                    }
                }

                // Sending BitField...
                BitField d = new BitField(curProcess.owned.getBitFieldByteArray());

                byte[] bitfieldObject = d.encode();
                output.write(bitfieldObject);
                ProcessManager.AllRemotePeerInfo.get(desPeerid).peerState = 8;
            }
            else{
                boolean waitforHandshakeRespond = true;
                while(waitforHandshakeRespond){
                    input.read(handshakeBuffer);
                    handshakeMsg = HandShake.decodeMessage(handshakeBuffer);
                    if(Util.convertByteToString(handshakeMsg.getHeader()).equals(HandShake.correctHeader)){
                        desPeerid = Util.convertByteToInt(handshakeMsg.getPeerID());
                        Util.PrintLog(curPeerid + "makes a connection to Peer " + desPeerid);
                        Util.PrintLog(curPeerid + " Received a HANDSHAKE message from Peer  " + desPeerid);
                        ProcessManager.despeerIdToSocket.put(desPeerid, this.socket);
                        waitforHandshakeRespond = false;
                    }
                }

                if(!sendHandShake()){
                    Util.PrintLog(this.curPeerid + " HANDSHAKE sending failed.");
                }
                else{
                    Util.PrintLog(this.curPeerid + " HANDSHAKE has been send.");
                }
                ProcessManager.AllRemotePeerInfo.get(desPeerid).peerState = 2;
            }
            boolean waitingForContinuousFollowedMessage = true;
            while(waitingForContinuousFollowedMessage)
            {

                int readStatusCode = input.read(messageHeader);

                if(readStatusCode == -1)
                    break;

                byte[] messageLength = new byte[4];
                byte[] messageType = new byte[1];
                System.arraycopy(messageHeader, 0, messageLength, 0, messageLength.length);
                System.arraycopy(messageHeader, messageLength.length, messageType, 0, messageType.length);
                int type = messageType[0];
                if(hasPayLoad(type)){
                    int bytesRead = 0;
                    int readStatus;
                    byte []dataPayload = new byte[Util.convertByteToInt(messageLength)-1];
                    while(bytesRead< Util.convertByteToInt(messageLength)-1){
                        readStatus = input.read(dataPayload, bytesRead, Util.convertByteToInt(messageLength)-1-bytesRead);
                        if(readStatus == -1) {
                            //finished reading
                            return;
                        }
                        bytesRead += readStatus;
                    }

                    System.out.println("finsihed reading");
                    ProcessManager.addToMessageQueuelist(new MessageWrapper(type,dataPayload,this.desPeerid));
                }
                else{
                    ProcessManager.addToMessageQueuelist(new MessageWrapper(type,null,this.desPeerid));
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean hasPayLoad(int type){
        return type > 3;
    }

}
