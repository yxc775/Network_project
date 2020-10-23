package HandShake;

import Peer.RemotePeerInfo;

public class HandShake {
    //HandShake message which will be sent after each initilized peer processs
    public final String header = "P2PFILESHARINGPROJ";
    public final String zeroBit = "0000000000";
    public String peerId;
    public HandShake(RemotePeerInfo x){
        this.peerId = x.peerId;
    }


}
