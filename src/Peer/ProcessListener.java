package Peer;

import java.net.ServerSocket;


//this is designed for handling receive message, take role as receiver. receiving message and call peerProcess to send message
public class ProcessListener implements  Runnable{
    private int ProcessID; // this will be the origional process id
    private ServerSocket ListeningSocket;// this will be the socket we used

    public ProcessListener(ServerSocket socket, int peerID){

    }

    //this will take as a thread and receiving message from other process
    public void run()
    {

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
