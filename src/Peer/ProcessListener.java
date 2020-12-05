package Peer;

import Utility.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


//this is designed for handling receive message, take role as receiver. receiving message and call peerProcess to send message
public class ProcessListener implements  Runnable{
    private ServerSocket listeningSocket;// this will be the socket we used
    private Socket peerSocket;
    private peerProcess curProcess;
    private int peerID;
    public Thread sendingThread;


    public ProcessListener(ServerSocket ListeningSocket, peerProcess curProcess, int peerID){
        this.listeningSocket = ListeningSocket;
        this.curProcess = curProcess;
        this.peerID = peerID;
    }

    //this will take as a thread and receiving message from other process
    public void run()
    {
        while(true){
            try {
                peerSocket = listeningSocket.accept();
                sendingThread = new Thread(new RemoteHandler(curProcess,peerSocket,false,peerID));
                Util.PrintLog(peerID + " : The connection is established");
                ProcessManager.sendingThread.add(sendingThread);
                sendingThread.start();

            }catch(IOException e){
                Util.PrintLog(this.peerID + " Exception in connection: " + e.toString());
            }
        }
    }
}
