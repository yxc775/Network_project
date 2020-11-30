package Peer;

//this is the infomation for different peer, other peer using this to connect with other peer
public class RemotePeerInfo implements Comparable<RemotePeerInfo>{
	public double downloadSpeed = 0;
	public int peerId;
	public String peerAddress;
	public int port;
	public boolean hasFile = false;
	public int index; //0 represents first peer
	public Boolean isChoked = false;
	public Boolean isCompleted = false;
	public Boolean isHandShaked = false;


	public RemotePeerInfo(int pId, String pAddress, int pPort, int index) {
		this.peerId = pId;
		this.peerAddress = pAddress;
		this.port = pPort;
		this.index = index;
	}

	public int  getPeerId(){
		return peerId;
	}

	public String getPeerAddress(){
		return peerAddress;
	}

	public int getPort(){
		return port;
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
