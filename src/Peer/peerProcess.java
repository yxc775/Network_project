package Peer;

import Config.CommonInfoConfig;
import Logger.Logger;
import Message.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

//this is the main peerProcess, take role as sender. send message to other process
public class peerProcess implements Runnable{
    public ServerSocket ListeningSocket = null; //this will used for listening socket
    public RemotePeerInfo remotePeerInfo = null;
    public int PeerID;//this is current id
    public boolean isFinished = false; //if this peer finish download this variable will turned to true

<<<<<<< Updated upstream
    public peerProcess(String peerID){
        this.PeerID = Integer.parseInt(peerID);
    }
=======
    //this will goes over the current data and decide what kind of message we will do next steps, which will using other functions
    public void run()
    {

    }

>>>>>>> Stashed changes

    //this will update all peerinfo from peerinfo.cfg to Hashtable, and unchocked peer
    public static void readPeerInfo()
    {

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

<<<<<<< Updated upstream
    public static void main(String[] args){
        peerProcess process = new peerProcess(args[0]);
        try{
            //start logging message communication between peers
            Logger.start("log_peer_" + process.PeerID + ".log");

            CommonInfoConfig.readCommonInfo("Common.cfg");
            readPeerInfo();

            process.StartPreferPeer();

            //assuming this peer does not have the file
            boolean hasFile = false;
        }
        catch (Exception e){

        }
=======
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

>>>>>>> Stashed changes
    }
}
