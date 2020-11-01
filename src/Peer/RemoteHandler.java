package Peer;


import Logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//this will be running as a thread, for receive data from other process
public class RemoteHandler implements Runnable{
    private boolean isSender;
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private String curPeerid;
    //
    public RemoteHandler(Socket socket, boolean isSender, String curPeerID){
        this.socket = socket;
        this.isSender = isSender;
        this.curPeerid = curPeerID;
        try{
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run(){

    }

}
