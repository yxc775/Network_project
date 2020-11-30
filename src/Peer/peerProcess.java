package Peer;

import Config.CommonAttributes;
import Config.CommonInfoConfig;
import FileManager.BitFieldObject;
import Logger.Logger;
import MessageObjects.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

//this is the main peerProcess, take role as sender. send message to other process
public class peerProcess implements Runnable {
    public ServerSocket listeningSocket = null; //this will used for listening socket
    public Thread listeningThread = null;
    public RemotePeerInfo remotePeerInfo;
    public BitFieldObject owned = null;

    public peerProcess(RemotePeerInfo peerInfo) {
        this.remotePeerInfo = peerInfo;
    }

    //this will goes over the current data and decide what kind of message we will do next steps, which will using other functions
    public void run() {

    }


    //this will update all peerinfo from peerinfo.cfg to Hashtable, and unchocked peer
    public static RemotePeerInfo readPeerInfo(String processID) {
        //todo search the corresponding peerInfo to start the process
        return new RemotePeerInfo(1, "", 1, 1);
    }

    //this will call prefered peer to start transfer data
    public void createPreferPeer() {
        Iterator items = ProcessesManager.AllRemotePeerInfo.entrySet().iterator();
        while (items.hasNext()) {
            Map.Entry peerInfoPair = (Map.Entry) items.next();
            int key = (int) peerInfoPair.getKey();
            RemotePeerInfo val = (RemotePeerInfo) peerInfoPair.getValue();
            if (key != this.remotePeerInfo.peerId) {
                ProcessesManager.PreferedPeer.put(key, val);
            }
        }
    }

    //initlize listening Thread for this process
    public void startListeningThread() {
        try {
            this.listeningSocket = new ServerSocket(this.remotePeerInfo.port);

            //start listening thread
            this.listeningThread = new Thread(new ProcessListener(this.listeningSocket, this.remotePeerInfo.peerId));
            this.listeningThread.start();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Logger.stop();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.stop();
        }
    }

    //this will call unprefered peer to start transfer data
    public static void Unchock() {

    }

    //this will call log generator to create loc based on input string
    public static void PrintLog(String message) {

    }

    //send message to socket
    public void SendMessage(Socket socket, Message message) {

    }

    //this will create empty file for download data
    public void createEmptyFile(int peerID) {
        try {
            File dir = new File(String.valueOf(peerID));
            dir.mkdir();
            File created = new File(String.valueOf(peerID), CommonAttributes.filename);
            OutputStream os = new FileOutputStream(created, true);
            byte noneByte = 0;
            for (int i = 0; i < CommonAttributes.filesize; i++) {
                os.write(noneByte);
            }
            os.close();
        } catch (FileNotFoundException e) {
            //todo may need to log this
            System.out.println("Empty File creation filed");
            e.printStackTrace();
        } catch (IOException e) {
            //todo may need to log this
            System.out.println("OS initialization write in fail");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        peerProcess process = new peerProcess(readPeerInfo(args[0]));
        boolean isFirst = false;
        try {
            //start logging message communication between peers
            Logger.start("log_peer_" + process.getProcessID() + ".log");

            CommonInfoConfig.readCommonInfo("Common.cfg");

            process.createPreferPeer();

            ProcessesManager.AllRemotePeerInfo.put(process.getProcessID()
                    , process.remotePeerInfo);
            if (ProcessesManager.AllRemotePeerInfo.size() == 1) {
                isFirst = true;
            }

            //initialize the Bit field
            process.owned = new BitFieldObject();
            process.owned.checkOwndedBitField(String.valueOf(process.getProcessID()), isFirst);

            ProcessesManager.messageManager = new Thread(new MessageManager());
            ProcessesManager.messageManager.start();

            //If it is the first peer, first peer is assumed to have the whole file.
            //we have to assign a file to have the whole file at the very early stage,
            // otherwise BITTorrent in this context wont work.
            if (isFirst) {
                process.startListeningThread();
            } else {
                process.createEmptyFile(process.getProcessID());

                Enumeration<Integer> e = ProcessesManager.AllRemotePeerInfo.keys();
                while (e.hasMoreElements()) {
                    RemotePeerInfo peerInfo = ProcessesManager.AllRemotePeerInfo.get(e.nextElement());
                    if (process.getProcessIndex() > peerInfo.index) {
                        //initilize remote handler which is handling sending message including handshake
                        Thread temp = new Thread(new RemoteHandler(process.getPort(),
                                process.getProcessID(),
                                true,
                                process.getAddress()));
                        ProcessesManager.receivingThread.add(temp);
                        temp.start();
                    }
                }

                //create listening thread
                process.startListeningThread();
            }
            ProcessesManager.startPreferedPeersTimer();
            ProcessesManager.startUnchokedPeersTimer();

            boolean checkAlldone = ProcessesManager.allDone();
            while (!checkAlldone) {
                checkAlldone = ProcessesManager.allDone();
                if (checkAlldone) {
                    //todo log the information that all related peers are done
                    ProcessesManager.stopPreferedPeersTimer();
                    ProcessesManager.stopUnchokePeersTimer();
                    try {
                        //for every 10 seconds
                        Thread.currentThread();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (process.listeningThread.isAlive()) {
                        process.listeningThread.interrupt();
                    }
                    if (ProcessesManager.messageManager.isAlive()) {
                        ProcessesManager.messageManager.interrupt();
                    }

                    for (Thread x : ProcessesManager.receivingThread) {
                        if (x.isAlive()) {
                            x.interrupt();
                        }
                    }
                    for (Thread x : ProcessesManager.sendingThread) {
                        if (x.isAlive()) {
                            x.interrupt();
                        }
                    }
                } else {
                    try {
                        Thread.currentThread();
                        //Wait 60 seconds to check again
                        Thread.sleep(6000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Todo need to log that this peer is finished
        Logger.stop();
    }

    //this will return the peer ID
    public int getProcessID() {
        return remotePeerInfo.peerId;
    }

    public int getProcessIndex() {
        return remotePeerInfo.index;
    }

    public int getPort() {
        return remotePeerInfo.port;
    }

    public String getAddress() {
        return remotePeerInfo.peerAddress;
    }

    //various function we will use for sending data
    private void SendChoke() {

    }

    private void SendUnchoke() {

    }

    private void SendInterested() {

    }

    private void SendUnInterested() {

    }

    private void SendHave() {

    }

    private void SendBitfield() {

    }

    private void SendRequest() {

    }

    private void SendPiece() {


    }
}
