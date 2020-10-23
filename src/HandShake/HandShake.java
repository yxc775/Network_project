package HandShake;

import Peer.RemotePeerInfo;

public class HandShake {
    public final String header = "P2PFILESHARINGPROJ";
    public final String zeroBit = "0000000000";
    public String peerId;
    public HandShake(RemotePeerInfo x){
        this.peerId = x.peerId;
    }
}
