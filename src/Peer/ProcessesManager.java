package Peer;

import Config.CommonAttributes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;


//this is a process manager which can keep tracks of the state of multiple process to help logging and information sync
public class ProcessesManager {
    public static volatile Hashtable<String, RemotePeerInfo> AllRemotePeerInfo = new Hashtable<String,RemotePeerInfo>(); //stored all peer info
    public static volatile Hashtable<String, RemotePeerInfo> PreferedPeer = new Hashtable<String,RemotePeerInfo>(); // this is the prefered peer, which is chocked
    public static volatile Hashtable<String, RemotePeerInfo> unchockedPeer = new Hashtable<String, RemotePeerInfo>(); // this is the unprefered peer, which is unchocked
    public static volatile Timer preferedPeerTimer;
    public static volatile Timer unchokedPeerTimer;
    public static Vector<Thread> receivingThread = new Vector<Thread>();
    public static Vector<Thread> sendingThread = new Vector<Thread>();
    public static Thread messageManager;

    public static void startPreferedPeersTimer(){
        preferedPeerTimer = new Timer();
        preferedPeerTimer.schedule(new PreferedPeer(),
                CommonAttributes.unChokeInterval * 1000,
                CommonAttributes.unChokeInterval * 1000
                );
    }

    public static void startUnchokedPeersTimer(){
        unchokedPeerTimer = new Timer();
        unchokedPeerTimer.schedule(new UnchokePeer(),
                CommonAttributes.optimisticUnchokeInterval* 1000,
                CommonAttributes.optimisticUnchokeInterval * 1000
        );
    }

    public static void stopPreferedPeersTimer(){
        preferedPeerTimer.cancel();
    }

    public static void stopUnchokePeersTimer(){
        unchokedPeerTimer.cancel();
    }

    public static synchronized  boolean allDone(){
        String line;
        int checkhasFile = 1;
        try{
            BufferedReader input = new BufferedReader(new FileReader("PeerInfo.cfg"));
            line = input.readLine();
            while(line != null){
                String hasfile = line.trim().split("\\s+")[3];
                checkhasFile = checkhasFile * Integer.parseInt(hasfile);
                line = input.readLine();
            }
            input.close();

            return checkhasFile == 1;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
