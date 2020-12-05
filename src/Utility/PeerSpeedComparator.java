package Utility;

import Peer.RemotePeerInfo;
import java.util.Comparator;

public class PeerSpeedComparator implements Comparator<RemotePeerInfo> {
    public int compare(RemotePeerInfo rm1, RemotePeerInfo rm2){
        if(rm1 == null && rm2 == null){
            return 0;
        }
        else if(rm1 == null){
            return 1;
        }
        else if(rm2 == null){
            return -1;
        }
        else{
            return rm2.compareTo(rm1);
        }
    }
}
