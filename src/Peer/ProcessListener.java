package Peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


//this is designed for handling receive message, take role as receiver. receiving message and call peerProcess to send message
public class ProcessListener implements  Runnable{
    private int ProcessID; // this will be the origional process id
    private ServerSocket listeningSocket;// this will be the socket we used
    private Socket peerSocket;
    private int peerID;
    public Thread sendingThread;

    public ProcessListener(){

    }

    public ProcessListener(ServerSocket ListeningSocket, int peerID){
        this.listeningSocket = ListeningSocket;
        this.peerID = peerID;
    }

    //this will take as a thread and receiving message from other process
    public void run()
    {
        while(true){
            try {
                peerSocket = listeningSocket.accept();
                sendingThread = new Thread(new RemoteHandler( peerSocket.getLocalPort(), peerID, false,
                        peerSocket.getLocalSocketAddress().toString()));
                peerProcess.PrintLog(peerID + " : The connection is established");
                ProcessesManager.sendingThread.add(sendingThread);
                sendingThread.start();

            }catch(IOException e){
                peerProcess.PrintLog(this.peerID + " Exception in connection: " + e.toString());
            }
        }


    }

    public void closeSocket()
    {
        try
        {
            if(!peerSocket.isClosed())
                peerSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //this will initialize this class. such as id and socket
    public void Initialize(int id, ServerSocket socket)
    {
        ProcessID = id;
        ListeningSocket = socket;
    }


    //here we have different function for handling receiving different message
    private void ReceiveChoke()
    {

    }

    private void ReceiveUnchoke()
    {

    }

    private void ReceiveInterested()
    {

    }

    private void ReceivedUnInterested()
    {

    }

    private void ReceivedHave()
    {

    }

    private void ReceivedBitfield()
    {

    }

    private void ReceivedRequest()
    {

    }

    private void ReceivedPiece()
    {

    }
}
