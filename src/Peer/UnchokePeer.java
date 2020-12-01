package Peer;

import Utility.Util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.TimerTask;
import java.util.Vector;

//this class will be running as a thread
public class UnchokePeer extends TimerTask {


    int peerID;

    public void SetID(int ID)
    {
        peerID = ID;
    }


    @Override
    public void run() {

        //update all hash in process manager

        //todo update unchokedpeer before this start
        //if unchoked peer is not empty we need clear it
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
                //todo send have
                //todo discuss what state is
            }
        }

    }
}