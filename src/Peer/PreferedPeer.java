package Peer;


import Config.CommonAttributes;
import Utility.PeerSpeedComparator;
import Utility.Util;
import MessageObjects.MessageWrapper;

import java.io.*;
import java.net.Socket;
import java.util.*;

//this class will run as a thread, to keep sending data out
public class PreferedPeer extends TimerTask {
    private int curpeerID;
    private peerProcess curProcess;
    public PreferedPeer(peerProcess curprocess){
        this.curProcess = curprocess;
        this.curpeerID = curprocess.remotePeerInfo.peerId;
    }
    @Override
    public void run() {
        reReadPeerInfo();
        String prelist = "";
        Enumeration<Integer> keys = ProcessManager.AllRemotePeerInfo.keys();
        int countNumOfInterest = 0;
        while(keys.hasMoreElements()){
            int key = keys.nextElement();
            RemotePeerInfo candidate = ProcessManager.AllRemotePeerInfo.get(key);
            if(key != curpeerID){
                if(!candidate.isCompleted && candidate.isHandShaked){
                    countNumOfInterest++;
                }
                else if(candidate.isCompleted){
                    try{
                        ProcessManager.PreferedPeer.remove(key);
                    }
                    catch(Exception e){
                        System.out.println("accessing PreferedPeer globalist fail");
                        e.printStackTrace();
                    }
                }
            }
        }

        //Need to pick the best neighbors because this process is interested to more nodes more than the setting configuration
        if(countNumOfInterest > CommonAttributes.numberOfPreferedN){
            if(!ProcessManager.PreferedPeer.isEmpty()){
                ProcessManager.PreferedPeer.clear();
            }
            List<RemotePeerInfo> pickList = new ArrayList<>(ProcessManager.AllRemotePeerInfo.values());
            Collections.sort(pickList, new PeerSpeedComparator());
            int count = 0;
            for(RemotePeerInfo i: pickList){
                if(count > CommonAttributes.numberOfPreferedN - 1){
                    break;
                }
                if(i.isHandShaked && i.peerId != curpeerID && !ProcessManager.AllRemotePeerInfo.get(i.peerId).isCompleted){
                    ProcessManager.AllRemotePeerInfo.get(i.peerId).isPrefered = true;
                    ProcessManager.PreferedPeer.put(i.peerId, ProcessManager.AllRemotePeerInfo.get(i.peerId));
                    count++;
                    prelist = prelist + i.peerId + ", ";
                    if(i.isChoked){
                        SendUnchoke(ProcessManager.despeerIdToSocket.get(i.peerId),i.peerId);
                        ProcessManager.AllRemotePeerInfo.get(i.peerId).isChoked = false;
                        SendHave(ProcessManager.despeerIdToSocket.get(i.peerId),i.peerId);
                        ProcessManager.AllRemotePeerInfo.get(i.peerId).peerState = 3;
                    }
                }
            }
        }
        else{
            while(keys.hasMoreElements()){
                int key = keys.nextElement();
                RemotePeerInfo candidate = ProcessManager.AllRemotePeerInfo.get(key);
                if(key != curpeerID){
                    if(!candidate.isCompleted && candidate.isHandShaked){
                        prelist = prelist + key + ", ";
                        ProcessManager.PreferedPeer.put(key, ProcessManager.AllRemotePeerInfo.get(key));
                        ProcessManager.AllRemotePeerInfo.get(key).isPrefered = true;
                    }
                    if(candidate.isChoked){
                        SendUnchoke(ProcessManager.despeerIdToSocket.get(key),key);
                        ProcessManager.AllRemotePeerInfo.get(key).isChoked = false;
                        SendHave(ProcessManager.despeerIdToSocket.get(key),key);
                        ProcessManager.AllRemotePeerInfo.get(key).peerState = 3;
                    }
                }
            }
        }
        if(prelist != ""){
            Util.PrintLog(curpeerID + " has selected the prefered neighbors - " + prelist);
        }
    }

    public static void reReadPeerInfo(){
        try{
            String line;
            BufferedReader input = new BufferedReader(new FileReader("PeerInfo.cfg"));
            line = input.readLine();
            while(line != null){
                String[] items = line.trim().split("\\s+");
                boolean isDownloaded = Integer.parseInt(items[3]) == 1;
                int peerid = Integer.parseInt(items[0]);
                if(isDownloaded){
                    ProcessManager.AllRemotePeerInfo.get(peerid).isCompleted = true;
                    ProcessManager.AllRemotePeerInfo.get(peerid).isInterested= false;
                    ProcessManager.AllRemotePeerInfo.get(peerid).isChoked = false;
                }
            }
            input.close();
        }
        catch(FileNotFoundException e){
            System.out.println("invalid file name");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("read line fail");
            e.printStackTrace();
        }

    }

   private void SendUnchoke(Socket socket, int remotePeerID) {
        Util.PrintLog(curpeerID + " is sending UNCHOKE message to remote Peer " + remotePeerID);
        MessageWrapper m = new MessageWrapper(1, null, remotePeerID);
        SendData(socket, m.encode());
    }
    private void SendHave(Socket socket, int remotePeerID) {
        byte[] encodedBitField = curProcess.owned.getBitFieldByteArray();
        Util.PrintLog(curpeerID + " sending HAVE message to Peer " + remotePeerID);
        MessageWrapper m = new MessageWrapper(4, encodedBitField, remotePeerID);
        SendData(socket,m.encode());
    }

    private static int SendData(Socket socket, byte[] encodedBitField) {
        try {
            OutputStream output = socket.getOutputStream();
            output.write(encodedBitField);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
