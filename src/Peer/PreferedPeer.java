package Peer;


import Config.CommonAttributes;
import Util.PeerSpeedComparator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

//this class will run as a thread, to keep sending data out
public class PreferedPeer extends TimerTask {
    public int curpeerID;
    public PreferedPeer(int peerId){
        curpeerID = peerId;
    }
    @Override
    public void run() {
        reReadPeerInfo();
        String prelist = "";
        Enumeration<Integer> keys = ProcessesManager.AllRemotePeerInfo.keys();
        int countNumOfInterest = 0;
        while(keys.hasMoreElements()){
            int key = keys.nextElement();
            RemotePeerInfo candidate = ProcessesManager.AllRemotePeerInfo.get(key);
            if(key != curpeerID){
                if(!candidate.isCompleted && candidate.isHandShaked){
                    countNumOfInterest++;
                }
                else if(candidate.isCompleted){
                    try{
                        ProcessesManager.PreferedPeer.remove(key);
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
            if(!ProcessesManager.PreferedPeer.isEmpty()){
                ProcessesManager.PreferedPeer.clear();
            }
            List<RemotePeerInfo> pickList = new ArrayList<>(ProcessesManager.AllRemotePeerInfo.values());
            //todo need to finish Peer streaming speed Comparator
            Collections.sort(pickList, new PeerSpeedComparator());
            int count = 0;
            for(RemotePeerInfo i: pickList){
                if(count > CommonAttributes.numberOfPreferedN - 1){
                    break;
                }
                if(i.isHandShaked && i.peerId == curpeerID && ProcessesManager.AllRemotePeerInfo.get(i.peerId).isCompleted){
                    ProcessesManager.AllRemotePeerInfo.get(i.peerId).isPrefered = true;
                    ProcessesManager.PreferedPeer.put(i.peerId,ProcessesManager.AllRemotePeerInfo.get(i.peerId));
                    count++;
                    prelist = prelist + i.peerId + ", ";
                    if(i.isChoked){
                        SendUnchoke(ProcessesManager.despeerIdToSocket.get(i.peerId),i.peerId);
                        ProcessesManager.AllRemotePeerInfo.get(i.peerId).isChoked = false;
                        SendHave(ProcessesManager.despeerIdToSocket.get(i.peerId),i.peerId);
                        ProcessesManager.AllRemotePeerInfo.get(i.peerId).peerState = 3;
                    }
                }
            }
        }
        else{
            while(keys.hasMoreElements()){
                int key = keys.nextElement();
                RemotePeerInfo candidate = ProcessesManager.AllRemotePeerInfo.get(key);
                if(key != curpeerID){
                    if(!candidate.isCompleted && candidate.isHandShaked){
                        prelist = prelist + key + ", ";
                        ProcessesManager.PreferedPeer.put(key,ProcessesManager.AllRemotePeerInfo.get(key));
                        ProcessesManager.AllRemotePeerInfo.get(key).isPrefered = true;
                    }
                    if(candidate.isChoked){
                        SendUnchoke(ProcessesManager.despeerIdToSocket.get(key),key);
                        ProcessesManager.AllRemotePeerInfo.get(key).isChoked = false;
                        SendHave(ProcessesManager.despeerIdToSocket.get(key),key);
                        ProcessesManager.AllRemotePeerInfo.get(key).peerState = 3;
                    }
                }
            }
        }
        //todo need to log this line after the prefered candidates are selected
        if(prelist != ""){
            System.out.println(curpeerID + " has selected the prefered neighbors - " + prelist);
        }
    }

    public void reReadPeerInfo(){
        try{
            String line;
            BufferedReader input = new BufferedReader(new FileReader("PeerInfo.cfg"));
            line = input.readLine();
            while(line != null){
                String[] items = line.trim().split("\\s+");
                boolean isDownloaded = Integer.parseInt(items[3]) == 1;
                int peerid = Integer.parseInt(items[0]);
                if(isDownloaded){
                    ProcessesManager.AllRemotePeerInfo.get(peerid).isCompleted = true;
                    ProcessesManager.AllRemotePeerInfo.get(peerid).isInterested= false;
                    ProcessesManager.AllRemotePeerInfo.get(peerid).isChoked = false;
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
        //todo implement send unchoke
    }
    private void SendHave(Socket socket, int remotePeerID) {
        //todo implement send have
    }
}
