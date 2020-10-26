package Peer;

import Message.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.Hashtable;

//this is the main peerProcess
public class peerProcess {
    public ServerSocket ListeningSocket = null; //this will used for listening socket
    public int PeerID;//this is current id
    public static volatile Hashtable<String, RemotePeerInfo> AllRemotePeerInfo = new Hashtable<String,RemotePeerInfo>(); //stored all peer info
    public static volatile Hashtable<String, RemotePeerInfo> PreferedPeer = new Hashtable<String,RemotePeerInfo>(); // this is the prefered peer, which is chocked
    public static volatile Hashtable<String, RemotePeerInfo> unchockedPeer = new Hashtable<String, RemotePeerInfo>(); // this is the unprefered peer, which is unchocked
    public boolean isFinished = false; //if this peer finish download this variable will turned to true


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
    public static void PringLog(String message)
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
}
