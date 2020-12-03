package Peer;

import MessageObjects.MessageWrapper;
import Utility.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TimerTask;
import java.util.Vector;

//this class will be running as a thread
public class UnchokePeer extends TimerTask {


    int peerID;
    private peerProcess curProcess;
    public UnchokePeer(peerProcess curprocess){
        this.curProcess = curprocess;
        this.peerID = curprocess.remotePeerInfo.peerId;
    }

    public void SetID(int ID)
    {
        peerID = ID;
    }


    @Override
    public void run() {

        //update all hash in process manager

        //todo update unchokedpeer before this start
        //if unchoked peer is not empty we need clear it
        PreferedPeer.reReadPeerInfo();
        if(ProcessManager.unchokedPeer.size() > 0)
        {
            ProcessManager.unchokedPeer.clear();
        }
        Enumeration<Integer> keys = ProcessManager.AllRemotePeerInfo.keys();
        Vector<RemotePeerInfo> temp = new Vector<RemotePeerInfo>(); //we create a temp vector for later sending usage

        while(keys.hasMoreElements())
        {
            int key = keys.nextElement();
            RemotePeerInfo tempinfo = ProcessManager.AllRemotePeerInfo.get(key);

            //if our status is choked and not complete, then we add it to later sending
            if(tempinfo.isChoked && key != peerID && !tempinfo.isCompleted && tempinfo.isHandShaked)
            {
                temp.add(tempinfo);
            }
        }


        //randomize the vector element
        //send the information
        if(temp.size() > 0 )
        {
            Collections.shuffle(temp);
            RemotePeerInfo peer = temp.firstElement();

            //todo printlog is not finished yet
            Util.PrintLog(peerID + " has the optimistically unchoked neighbor " + peer.peerId);

            if( ProcessManager.AllRemotePeerInfo.get(peer.peerId).isChoked )
            {
                ProcessManager.AllRemotePeerInfo.get(peer.peerId).isChoked = false;
                //todo send unchoke
                SendUnchoke(ProcessManager.despeerIdToSocket.get(peer.peerId),peer.peerId);
                //todo send have
                SendHave(ProcessManager.despeerIdToSocket.get(peer.peerId),peer.peerId);
                //todo discuss what state is
                ProcessManager.AllRemotePeerInfo.get(peer.peerId).peerState = 3;
            }
        }

    }

    private void SendUnchoke(Socket socket, int remotePeerID) {
        Util.PrintLog(peerID + " is sending UNCHOKE message to remote Peer " + remotePeerID);
        MessageWrapper m = new MessageWrapper(1, null, remotePeerID);
        SendData(socket, m.encode());
    }

    private void SendHave(Socket socket, int remotePeerID) {
        byte[] encodedBitField = curProcess.owned.getBitFieldByteArray();
        Util.PrintLog(peerID + " sending HAVE message to Peer " + remotePeerID);
        MessageWrapper m = new MessageWrapper(4, encodedBitField, remotePeerID);
        SendData(socket,m.encode());
    }

    private static int SendData(Socket socket, byte[] encodedBitField) {
        try {
            OutputStream output = socket.getOutputStream();
            output.write(encodedBitField);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
