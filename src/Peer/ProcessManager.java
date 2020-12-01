package Peer;

import Config.CommonAttributes;
import MessageObjects.MessageWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Socket;
import java.util.*;


//this is a process manager which can keep tracks of the state of multiple process to help logging and information sync including message processing
//from multiple senders
public class ProcessManager {
    public static volatile Hashtable<Integer, RemotePeerInfo> AllRemotePeerInfo = new Hashtable<Integer,RemotePeerInfo>(); //stored all peer info
    public static volatile Hashtable<Integer, RemotePeerInfo> PreferedPeer = new Hashtable<Integer,RemotePeerInfo>(); // this is the prefered peer, which is chocked
    public static volatile Hashtable<Integer, RemotePeerInfo> unchokedPeer = new Hashtable<Integer, RemotePeerInfo>(); // this is the unprefered peer, which is unchocked
    //despeerIdToSocket map store all the destination peer process id corresponding to each socket, so this process can send message to them
    public static Hashtable<Integer, Socket> despeerIdToSocket = new Hashtable<Integer, Socket>();
    public static Vector<Thread> receivingThread = new Vector<Thread>();
    public static Vector<Thread> sendingThread = new Vector<Thread>();
    public static volatile Queue<MessageWrapper> messageQ = new LinkedList<MessageWrapper>();
    public static Thread messageManager;

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

    public static synchronized void addToMsgQueue(MessageWrapper msg){
        messageQ.add(msg);
    }

}
