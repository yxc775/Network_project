package Peer;


import HandShake.HandShake;
import Utility.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//this will be running as a thread, for receive data from other process
public class RemoteHandler implements Runnable{
    private Socket socket = null;
    private HandShake handshakeMsg;
    private int MESSAGE_LENTH_BYTE = 4;
    private int MESSAGE_TYPE_BYTE = 1;

    private InputStream input;
    private OutputStream output;
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
        if(isSender){
            try{
                socket = new Socket(address,portnum);
            }
            catch(IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        else{
            if(socket == null){
                Util.PrintLog("trying to receiving passive connection without assigned socket");
                System.exit(0);
            }
            try{
                input = socket.getInputStream();
                output = socket.getOutputStream();
            }
            catch(Exception ex){
                Util.PrintLog(this.curPeerid + " error: " + ex.getMessage());
                System.exit(0);
            }
        }



    }

    public boolean startHandShake(){
        return false;
    }
    public void run(){
        byte[] handshakeBuffer = new byte[32];
        byte[] dataWithoutPayload = new byte[MESSAGE_LENTH_BYTE + MESSAGE_TYPE_BYTE];
        try{
            if(this.isSender){
                if(!startHandShake()){
                    Util.PrintLog(this.curPeerid + " HANDSHAKE sending failed.");
                }
                else{
                    Util.PrintLog(this.curPeerid + " HANDSHAKE has been send.");
                }
                boolean waitforHandshakeRespond = true;
                while(waitforHandshakeRespond){
                    input.read(handshakeBuffer);
                    //todo need to finish this class
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
