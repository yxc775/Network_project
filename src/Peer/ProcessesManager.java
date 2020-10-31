package Peer;

import java.util.Hashtable;


//this is a process manager which can keep tracks of the state of multiple process to help logging and information sync
public class ProcessesManager {
    public static volatile Hashtable<String, RemotePeerInfo> AllRemotePeerInfo = new Hashtable<String,RemotePeerInfo>(); //stored all peer info
    public static volatile Hashtable<String, RemotePeerInfo> PreferedPeer = new Hashtable<String,RemotePeerInfo>(); // this is the prefered peer, which is chocked
    public static volatile Hashtable<String, RemotePeerInfo> unchockedPeer = new Hashtable<String, RemotePeerInfo>(); // this is the unprefered peer, which is unchocked

}
