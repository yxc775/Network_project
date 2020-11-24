package Peer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//this will be running as a thread, for receive data from other process
public class RemoteHandler implements Runnable{
    private boolean isSender;
    private int port;
    private String address;
    private int curPeerid;
    //
    public RemoteHandler(int portnum,int curPeerID, boolean isSender, String address){
        this.port = portnum;
        this.isSender = isSender;
        this.address = address;
        this.curPeerid = curPeerID;

    }

    public void run(){

    }

}
