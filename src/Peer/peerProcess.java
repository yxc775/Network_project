package Peer;

import Config.CommonInfoConfig;
import FileManager.BitFieldObject;
import Logger.Logger;
import MessageObjects.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

//this is the main peerProcess, take role as sender. send message to other process
public class peerProcess implements Runnable{
    public ServerSocket ListeningSocket = null; //this will used for listening socket
    public RemotePeerInfo remotePeerInfo = null;
    public BitFieldObject owned = null;
    public int PeerID;

    public peerProcess(RemotePeerInfo peerInfo){
        this.remotePeerInfo = peerInfo;
    }

    //this will goes over the current data and decide what kind of message we will do next steps, which will using other functions
    public void run()
    {

    }



    //this will update all peerinfo from peerinfo.cfg to Hashtable, and unchocked peer
    public static RemotePeerInfo readPeerInfo(String processID)
    {
        //todo search the corresponding peerInfo to start the process
        return new RemotePeerInfo(1,"",1);
    }

    //this will call prefered peer to start transfer data
    public void StartPreferPeer()
    {
        Iterator items = ProcessesManager.AllRemotePeerInfo.entrySet().iterator();
        while(items.hasNext())
        {
            Map.Entry peerInfoPair = (Map.Entry)items.next();
            String key = (String)peerInfoPair.getKey();
            RemotePeerInfo val = (RemotePeerInfo)peerInfoPair.getValue();
            if(!key.equals(this.PeerID))
            {
                ProcessesManager.PreferedPeer.put(key,val);
            }
        }
    }

    //this will call unprefered peer to start transfer data
    public static void Unchock()
    {

    }

    //this will call log generator to create loc based on input string
    public static void PrintLog(String message)
    {

    }

    //send message to socket
    public void SendMessage(Socket socket, Message message)
    {

    }

    //this will create empty file for download data
    public void CreateEmptyFile()
    {

    }


    public static void main(String[] args) {
        RemotePeerInfo info = readPeerInfo(args[0]);
        peerProcess process = new peerProcess(info);
        boolean isFirst = false;
        try {
            //start logging message communication between peers
            Logger.start("log_peer_" + process.PeerID + ".log");

            CommonInfoConfig.readCommonInfo("Common.cfg");

            process.StartPreferPeer();

            ProcessesManager.AllRemotePeerInfo.put(String.valueOf(process.remotePeerInfo.peerId)
                    ,process.remotePeerInfo);
            if(ProcessesManager.AllRemotePeerInfo.size() == 1){
                isFirst = true;
            }

            //initialize the Bit field
            process.owned = new BitFieldObject();
            process.owned.checkOwndedBitField(String.valueOf(process.PeerID),isFirst);


        } catch (Exception e) {

        }
    }

    //this will return the peer ID
    public int GetProcessID()
    {
        return PeerID;
    }


    //various function we will use for sending data
    private void SendChoke()
    {

    }

    private void SendUnchoke()
    {

    }

    private void SendInterested()
    {

    }

    private void SendUnInterested()
    {

    }

    private void SendHave()
    {

    }

    private void SendBitfield()
    {

    }

    private void SendRequest()
    {

    }

    private void SendPiece()
    {


    }
}
