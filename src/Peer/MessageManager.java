package Peer;

import Config.CommonAttributes;
import FileManager.FilePiecesState;
import MessageObjects.*;
import Utility.Util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

public class MessageManager implements  Runnable {
    private boolean running = true;

    public peerProcess curprocess;

    public MessageManager(peerProcess process)
    {
        this.curprocess = process;
    }

    public boolean checkIsInterested(Message x, int despeerId){
        FilePiecesState s = FilePiecesState.convertBitFieldToFileInfo(x.getPayload());
        ProcessManager.AllRemotePeerInfo.get(despeerId).filesState = s;
        return curprocess.owned.checkAnyFileListMismatch(s);
    }

    public void run()
    {
        Message message;
        MessageWrapper messageWrapper;
        int type;
        int despeerId;
        while(running){
            messageWrapper = ProcessManager.removeFromMsgQueue();
            /*Waiting for arriving message if message queue is empty*/
            while(messageWrapper == null){
                Thread.currentThread();
                try{
                    Thread.sleep(500);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                messageWrapper = ProcessManager.removeFromMsgQueue();
            }

            message = messageWrapper.getMessage();
            type = message.getMessageType();
            despeerId = messageWrapper.getRemoteSenderID();
            int state = ProcessManager.AllRemotePeerInfo.get(despeerId).peerState;
            //need to precheck it for debug reason
            if(type == MessageWrapper.HAVE_TYPE && state != 14){
                Util.PrintLog(curprocess.remotePeerInfo.peerId + " received HAVE message from Peer" + despeerId);
                if(checkIsInterested(message,despeerId)){
                    sendInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                    ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 9;
                }
                else{
                    sendNotInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                    ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 13;
                }
            }
            else{
                switch(state){
                    case 2:
                        if(type == MessageWrapper.BITFIELD_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " received a BITFIELD message from Peer " + despeerId);
                            sendBitField(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 3;
                        }
                        break;
                    case 3:
                        if(type == MessageWrapper.NOTINTERESTED_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " received a NOT INTERESTED message from Peer " + despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).isInterested = false;
                            ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 5;
                            ProcessManager.AllRemotePeerInfo.get(despeerId).isHandShaked = true;
                        }
                        else if(type == MessageWrapper.INTERESTED_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " received an INTERESTED message from Peer " + despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).isInterested = true;
                            ProcessManager.AllRemotePeerInfo.get(despeerId).isHandShaked = true;

                            if (!ProcessManager.PreferedPeer.containsKey(despeerId) && !ProcessManager.unchokedPeer.containsKey(despeerId)) {
                                sendChoke(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).isChoked = true;
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 6;
                            }
                            else{
                                ProcessManager.AllRemotePeerInfo.get(despeerId).isChoked = false;
                                sendUnChoke(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 4;
                            }
                        }
                        break;
                    case 4:
                        if(type == MessageWrapper.REQUEST_TYPE){
                            sendPiece(ProcessManager.despeerIdToSocket.get(despeerId),message,despeerId);
                            if(!ProcessManager.PreferedPeer.containsKey(despeerId) && !ProcessManager.unchokedPeer.containsKey(despeerId)){
                                sendChoke(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).isChoked = true;
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 6;
                            }
                        }
                        break;
                    case 8:
                        if(type == MessageWrapper.BITFIELD_TYPE){
                            if(checkIsInterested(message,despeerId)){
                                sendInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 9;
                            }
                            else{
                                sendNotInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 13;
                            }
                        }
                        break;
                    case 9:
                        if(type == MessageWrapper.CHOKE_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " is CHOKED by Peer " + despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 14;
                        }
                        else if(type == MessageWrapper.UNCHOKE_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " is UNCHOKED by Peer " + despeerId);
                            int findMissingFile = curprocess.owned.findFirstFileListMisMatch(
                                    ProcessManager.AllRemotePeerInfo.get(despeerId).filesState);
                            if(findMissingFile != -1){
                                sendRequest(ProcessManager.despeerIdToSocket.get(despeerId),findMissingFile);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 11;
                                ProcessManager.AllRemotePeerInfo.get(despeerId).timeStart = new Date();
                            }
                            else{
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 13;
                            }
                        }
                        break;
                    case 11:
                        if(type == MessageWrapper.PIECE_TYPE){
                            byte[] buf = message.getPayload();
                            ProcessManager.AllRemotePeerInfo.get(despeerId).timeExit = new Date();
                            long timePast = ProcessManager.AllRemotePeerInfo.get(despeerId).timeExit.getTime() -
                                    ProcessManager.AllRemotePeerInfo.get(despeerId).timeStart.getTime();
                            ProcessManager.AllRemotePeerInfo.get(despeerId).downloadSpeed =
                                    ((double)(buf.length + 5)/(double)timePast) * 100;
                            Piece piece = new Piece(buf);
                            curprocess.updateFileBitPart(despeerId,piece);

                            int togetPiecePart = curprocess.owned.findFirstFileListMisMatch(ProcessManager.AllRemotePeerInfo.get(despeerId).filesState);

                            if(togetPiecePart != -1){
                                Util.PrintLog(curprocess.remotePeerInfo.peerId + " Requesting piece " +togetPiecePart +" from peer " +despeerId);
                                sendRequest(ProcessManager.despeerIdToSocket.get(despeerId),togetPiecePart);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 11;
                                ProcessManager.AllRemotePeerInfo.get(despeerId).timeStart = new Date();
                            }
                            else{
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 13;
                            }


                            peerProcess.updatePeerInfo();
                            Enumeration<Integer> keys = ProcessManager.AllRemotePeerInfo.keys();
                            while(keys.hasMoreElements()){
                                int key = keys.nextElement();
                                RemotePeerInfo info = ProcessManager.AllRemotePeerInfo.get(key);
                                if(key != curprocess.remotePeerInfo.peerId){
                                    if(!info.isCompleted && !info.isChoked && info.isHandShaked){
                                        sendHave(ProcessManager.despeerIdToSocket.get(key),key);
                                        ProcessManager.AllRemotePeerInfo.get(key).peerState = 3;
                                    }
                                }
                            }
                        }
                        else if(type == MessageWrapper.CHOKE_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " is CHOKED by Peer " +despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 14;
                        }
                        break;
                    case 14:
                        if(type == MessageWrapper.HAVE_TYPE){
                            if(checkIsInterested(message,despeerId)){
                                sendInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 9;
                            }
                            else{
                                sendNotInterested(ProcessManager.despeerIdToSocket.get(despeerId),despeerId);
                                ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 13;
                            }
                        }
                        else if(type == MessageWrapper.UNCHOKE_TYPE){
                            Util.PrintLog(curprocess.remotePeerInfo.peerId + " is UNCHOKED by Peer " + despeerId);
                            ProcessManager.AllRemotePeerInfo.get(despeerId).peerState = 14;
                        }
                        break;
                }
            }
        }
    }

    private static int sendData(Socket socket, byte[] rawData) {
        try {
            OutputStream output = socket.getOutputStream();
            output.write(rawData);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private void sendInterested(Socket socket, int desPeerId){
        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending an INTERESTED message to Peer " + desPeerId);
        Message d = new Interested();
        byte[] msg = d.encode();
        sendData(socket,msg);
    }

    private void sendNotInterested(Socket socket, int desPeerId){
        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending a NOT INTERESTED message to Peer " + desPeerId);
        Message d = new NotInterested();
        byte[] msg = d.encode();
        sendData(socket,msg);
    }

    private void sendBitField(Socket socket, int desPeerId) {

        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending BITFIELD message to Peer " + desPeerId);

        BitField x = new BitField( curprocess.owned.getBitFieldByteArray());
        sendData(socket,x.encode());
    }

    private void sendChoke(Socket socket, int desPeerId) {
        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending CHOKE message to Peer " + desPeerId);
        byte[] msg =new Choke().encode();
        sendData(socket,msg);
    }

    private void sendUnChoke(Socket socket,int desPeerId) {
        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending UNCHOKE message to Peer " + desPeerId);
        byte[] msg = new UnChoke().encode();
        sendData(socket,msg);
    }

    private void sendPiece(Socket socket, Message d, int desPeerId)
    {
        byte[] bytePieceIndex = d.getPayload();
        int index = Util.byteArrayToInt(bytePieceIndex);

        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending a PIECE message for piece " + index + " to Peer " + desPeerId);

        byte[] byteRead = new byte[CommonAttributes.piecesize];
        int numofBytesRead = 0;
        RandomAccessFile rf;

        File file = new File(String.valueOf(curprocess.remotePeerInfo.peerId),CommonAttributes.filename);
        try
        {
            rf = new RandomAccessFile(file,"r");
            rf.seek(index * CommonAttributes.piecesize);
            numofBytesRead = rf.read(byteRead, 0, CommonAttributes.piecesize);
            rf.close();
        }
        catch (IOException e)
        {
           Util.PrintLog(curprocess.remotePeerInfo.peerId + " ERROR in reading the file : " +  e.toString());
        }
        if(numofBytesRead == 0)
        {
           Util.PrintLog(curprocess.remotePeerInfo.peerId + " ERROR :  Zero bytes read from the file !");
        }
        else if (numofBytesRead < 0)
        {
            Util.PrintLog(curprocess.remotePeerInfo.peerId + " ERROR : File could not be read properly.");
        }

        byte[] buf = new byte[numofBytesRead +Piece.INDEX_LENGTH];
        System.arraycopy(bytePieceIndex, 0, buf, 0, Piece.INDEX_LENGTH);
        System.arraycopy(byteRead, 0, buf, Piece.INDEX_LENGTH, numofBytesRead);

        Piece p = new Piece(buf);
        sendData(socket, p.encode());
    }

    private void sendRequest(Socket socket, int pieceIndexNum) {
        byte[] toSend = new byte[Piece.INDEX_LENGTH];

        byte[] pieceIndexByte = Util.intToByteArray(pieceIndexNum);
        System.arraycopy(pieceIndexByte, 0, toSend, 0,
                pieceIndexByte.length);
        Request sendRequest = new Request(toSend);
        sendData(socket, sendRequest.encode());
    }

    private void sendHave(Socket socket, int desPeerID) {

        Util.PrintLog(curprocess.remotePeerInfo.peerId + " sending HAVE message to Peer " + desPeerID);
        byte[] ownedBitField = curprocess.owned.getBitFieldByteArray();
        Have d = new Have(ownedBitField);
        sendData(socket,d.encode());
    }





}
