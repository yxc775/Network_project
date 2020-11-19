package Peer;

//this is the infomation for different peer, other peer using this to connect with other peer
public class RemotePeerInfo implements Comparable<RemotePeerInfo>{
	public double downloadSpeed = 0;
	public String peerId;
	public String peerAddress;
	public String peerPort;
	public boolean hasFile = false;

	public RemotePeerInfo(String pId, String pAddress, String pPort) {
		peerId = pId;
		peerAddress = pAddress;
		peerPort = pPort;
	}

	public String getPeerId(){
		return peerId;
	}

	public String getPeerAddress(){
		return peerAddress;
	}

	public String getPeerPort(){
		return peerPort;
	}

	public boolean hasFile(){
		return hasFile;
	}

	public int compareTo(RemotePeerInfo o1) {
		if (this.downloadSpeed > o1.downloadSpeed)
			return 1;
		else if (this.downloadSpeed == o1.downloadSpeed)
			return 0;
		else
			return -1;
	}
}
