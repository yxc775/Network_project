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

}
