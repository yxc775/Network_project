package Peer;

import Config.CommonInfoConfig;
import Logger.Logger;
import Message.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.Hashtable;

//this is the main peerProcess
public class peerProcess {
    public ServerSocket ListeningSocket = null; //this will used for listening socket
    public RemotePeerInfo remotePeerInfo = null;
    public int PeerID;//this is current id
    public boolean isFinished = false; //if this peer finish download this variable will turned to true

    public peerProcess(String peerID){
        this.PeerID = Integer.parseInt(peerID);
    }

    //this will update all peerinfo from peerinfo.cfg to Hashtable, and unchocked peer
    public static void readPeerInfo()
    {

    }

    //this will call prefered peer to start transfer data
    public static void StartPreferPeer()
    {

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

    public static void main(String[] args){
        peerProcess process = new peerProcess(args[0]);
        try{
            //start logging message communication between peers
            Logger.start("log_peer_" + process.PeerID + ".log");

            CommonInfoConfig.readCommonInfo("Common.cfg");
            readPeerInfo();
            //TODO:initialize prefered peers

            //assuming this peer does not have the file
            boolean hasFile = false;
        }
        catch (Exception e){

        }
    }
}
