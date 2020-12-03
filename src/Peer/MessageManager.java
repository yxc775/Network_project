package Peer;

import java.io.RandomAccessFile;
import java.net.Socket;

public class MessageManager implements  Runnable {
    private boolean running = true;
    public static int peerState = -1;
    RandomAccessFile raf;

    public MessageManager()
    {
    }
    public void run()
    {
        //todo Implement this object which is used to observe and log every process informaiton

    }
    private void sendRequest(Socket socket, int pieceNo, String remotePeerID)
    {

    }

    private void sendNotInterested(Socket socket, String remotePeerID)
    {

    }

    private void sendInterested(Socket socket, String remotePeerID)
    {

    }

    private void sendUnChoke(Socket socket, String remotePeerID)
    {

    }

    private void sendChoke(Socket socket, String remotePeerID)
    {

    }

    private void sendBitField(Socket socket, String remotePeerID)
    {

    }

    private void sendHave(Socket socket, String remotePeerID)
    {

    }

    private int SendData(Socket socket, byte[] encodedBitField)
    {
        return 0;
    }
    //todo send isinterest
    //todo send peice
}
