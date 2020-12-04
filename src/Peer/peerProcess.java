package Peer;

import Config.CommonAttributes;
import Config.CommonInfoConfig;
import FileManager.FilePiecesState;
import Logger.Logger;
import MessageObjects.Message;
import MessageObjects.Piece;
import Utility.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

//this is the main peerProcess, take role as sender. send message to other process
public class peerProcess implements Runnable {
    public ServerSocket listeningSocket = null; //this will used for listening socket
    public Thread listeningThread = null;
    public RemotePeerInfo remotePeerInfo;
    public FilePiecesState owned = null;
    public static volatile Timer preferedPeerTimer;
    public static volatile Timer unchokedPeerTimer;

    public peerProcess() {
    }

    //this will goes over the current data and decide what kind of message we will do next steps, which will using other functions
    public void run() {

    }


    //this will update all peerinfo from peerinfo.cfg to Hashtable, and unchocked peer
    public static void readPeerInfo() {
        String line;
        try{
            BufferedReader input = new BufferedReader(new FileReader("PeerInfo.cfg"));
            int linecount = 0;
            line = input.readLine();
            while(line != null){
                String[] tokens = line.split("\\s+");
                int peerId = Integer.valueOf(tokens[0]);
                String address = tokens[1];
                int portnum = Integer.valueOf(tokens[2]);

                ProcessManager.AllRemotePeerInfo.put(Integer.valueOf(tokens[0]),new RemotePeerInfo(peerId,address,portnum,linecount));
                linecount++;
            }
            input.close();
        }
        catch(FileNotFoundException e){
            //todo need to log this line
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void updatePeerInfo()
    {
        try
        {
            String st;
            BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
            while ((st = in.readLine()) != null)
            {
                String[]args = st.trim().split("\\s+");
                String peerID = args[0];
                int isCompleted = Integer.parseInt(args[3]);
                if(isCompleted == 1)
                {
                    ProcessManager.AllRemotePeerInfo.get(peerID).isCompleted = true;
                    ProcessManager.AllRemotePeerInfo.get(peerID).isInterested = false;
                    ProcessManager.AllRemotePeerInfo.get(peerID).isChoked = false;
                }
            }
            in.close();
        }
        catch (Exception e) {
            Util.PrintLog(e.toString());
        }
    }

    //this will call prefered peer to start transfer data
    public void createPreferPeer() {
        Iterator items = ProcessManager.AllRemotePeerInfo.entrySet().iterator();
        while (items.hasNext()) {
            Map.Entry peerInfoPair = (Map.Entry) items.next();
            int key = (int) peerInfoPair.getKey();
            RemotePeerInfo val = (RemotePeerInfo) peerInfoPair.getValue();
            if (key != this.remotePeerInfo.peerId) {
                ProcessManager.PreferedPeer.put(key, val);
            }
        }
    }

    //initlize listening Thread for this process
    public void startListeningThread() {
        try {
            this.listeningSocket = new ServerSocket(this.remotePeerInfo.port);

            //start listening thread
            this.listeningThread = new Thread(new ProcessListener(this, this.listeningSocket, this.remotePeerInfo.peerId));
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
            Util.PrintLog("Empty File creation filed");
            e.printStackTrace();
        } catch (IOException e) {
            //todo may need to log this
            Util.PrintLog("OS initialization write in fail");
            e.printStackTrace();
        }
    }

    public void startPreferedPeersTimer(){
        preferedPeerTimer = new Timer();
        preferedPeerTimer.schedule(new PreferedPeer(this),
                CommonAttributes.unChokeInterval * 1000,
                CommonAttributes.unChokeInterval * 1000
        );
    }

    public void startUnchokedPeersTimer(){
        unchokedPeerTimer = new Timer();
        unchokedPeerTimer.schedule(new UnchokePeer(this),
                CommonAttributes.optimisticUnchokeInterval* 1000,
                CommonAttributes.optimisticUnchokeInterval * 1000
        );
    }

    public void stopPreferedPeersTimer(){
        preferedPeerTimer.cancel();
    }

    public void stopUnchokePeersTimer(){
        unchokedPeerTimer.cancel();
    }


    public static void main(String[] args) {
        peerProcess process = new peerProcess();
        boolean isFirst = false;
        try {
            //start logging message communication between peers
            Logger.start("log_peer_" + process.getProcessID() + ".log");

            CommonInfoConfig.readCommonInfo("Common.cfg");

            readPeerInfo();
            process.remotePeerInfo = ProcessManager.AllRemotePeerInfo.get(args[0]);

            process.createPreferPeer();

            if(process.remotePeerInfo.index == 0){
                isFirst = true;
            }

            //initialize the Bit field
            process.owned = new FilePiecesState();
            process.owned.checkOwndedBitField(String.valueOf(process.getProcessID()), isFirst);

            ProcessManager.messageManager = new Thread(new MessageManager(process));
            ProcessManager.messageManager.start();

            //If it is the first peer, first peer is assumed to have the whole file.
            //we have to assign a file to have the whole file at the very early stage,
            // otherwise BITTorrent in this context wont work.
            if (isFirst) {
                process.startListeningThread();
            } else {
                process.createEmptyFile(process.getProcessID());

                Enumeration<Integer> e = ProcessManager.AllRemotePeerInfo.keys();
                while (e.hasMoreElements()) {
                    RemotePeerInfo peerInfo = ProcessManager.AllRemotePeerInfo.get(e.nextElement());
                    if (process.getProcessIndex() > peerInfo.index) {
                        //initilize remote handler which is handling sending message including handshake
                        Thread temp = new Thread(new RemoteHandler(process,process.getPort(),
                                process.getProcessID(),
                                true,
                                process.getAddress()));
                        ProcessManager.receivingThread.add(temp);
                        temp.start();
                    }
                }

                //create listening thread
                process.startListeningThread();
            }
            process.startPreferedPeersTimer();
            process.startUnchokedPeersTimer();

            boolean checkAlldone = ProcessManager.allDone();
            while (!checkAlldone) {
                checkAlldone = ProcessManager.allDone();
                if (checkAlldone) {
                    //todo log the information that all related peers are done
                    process.stopPreferedPeersTimer();
                    process.stopUnchokePeersTimer();
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
                    if (ProcessManager.messageManager.isAlive()) {
                        ProcessManager.messageManager.interrupt();
                    }

                    for (Thread x : ProcessManager.receivingThread) {
                        if (x.isAlive()) {
                            x.interrupt();
                        }
                    }
                    for (Thread x : ProcessManager.sendingThread) {
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
        Util.PrintLog("Peer Process " + process.remotePeerInfo.getPeerId() + " is finished and exiting... ");
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

    public synchronized void updateFileBitPart(int peerId, Piece piece) {
        try
        {
            if (this.owned.filePiecesList[piece.index].haveit) {
                Util.PrintLog(peerId + " Piece already received!!");
            }
            else
            {
                String fileName = CommonAttributes.filename;
                File file = new File(String.valueOf(this.remotePeerInfo.peerId), fileName);
                int off = piece.index * CommonAttributes.piecesize;
                RandomAccessFile rf = new RandomAccessFile(file, "rw");
                byte[] filewrite = piece.getFilePart();

                rf.seek(off);
                rf.write(filewrite);

                this.owned.filePiecesList[piece.index].setHaveIt();
                this.owned.filePiecesList[piece.index].setFromPeer(peerId);
                rf.close();

               Util.PrintLog(this.remotePeerInfo.peerId
                        + " has downloaded the PIECE " + piece.index
                        + " from Peer " + peerId
                        + ". Now the number of pieces it has is "
                        + this.owned.countHavedPieces());

                if (this.owned.hasALLPieces()) {
                    ProcessManager.AllRemotePeerInfo.get(this.remotePeerInfo.peerId).isInterested = false;
                    ProcessManager.AllRemotePeerInfo.get(this.remotePeerInfo.peerId).isCompleted = true;
                    ProcessManager.AllRemotePeerInfo.get(this.remotePeerInfo.peerId).isChoked = false;
                    updatePeerInfoCgFile();

                    Util.PrintLog(this.remotePeerInfo.peerId + " has downloaded the complete file.");
                }
            }

        } catch (Exception e) {
            Util.PrintLog(this.remotePeerInfo.peerId
                    + " EROR in updating bitfield " + e.getMessage());
        }

    }

    // Updates PeerInfo.cfg
    public void updatePeerInfoCgFile()
    {
        BufferedWriter output = null;
        BufferedReader input = null;

        try
        {
            input= new BufferedReader(new FileReader("PeerInfo.cfg"));

            String line;
            String hasFullFile = "1";
            StringBuffer buffer = new StringBuffer();
            line = input.readLine();
            while(line != null)
            {
                String[] tokens = line.trim().split("\\s+");
                if(Integer.valueOf(tokens[0]) == this.remotePeerInfo.peerId)
                {
                    buffer.append(tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + hasFullFile);
                }
                else
                {
                    buffer.append(line);
                }
                buffer.append("\n");
            }

            input.close();

            output= new BufferedWriter(new FileWriter("PeerInfo.cfg"));
            output.write(buffer.toString());

            output.close();
        }
        catch (Exception e)
        {
            Util.PrintLog(this.remotePeerInfo.peerId + " Error in updating the PeerInfo.cfg " +  e.getMessage());
        }
    }



}
