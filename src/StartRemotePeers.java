import Peer.RemotePeerInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 * The StartRemotePeers class begins remote peer processes. 
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 * You must modify this program a little bit if your peer processes are written in C or C++.
 * Please look at the lines below the comment saying IMPORTANT.
 */
public class StartRemotePeers {

	public Vector<RemotePeerInfo> peerInfoVector;
	public Vector<Process> peerProcesses;
	
	public void getConfiguration()
	{
		String st;
		peerInfoVector = new Vector<RemotePeerInfo>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			int i = 1;
			while((st = in.readLine()) != null) {
				 String[] tokens = st.split("\\s+");
				 if(tokens[0].getBytes().length > 4){
				 	throw new IOException("processID cannot be more than 4 bytes");
				 }
			     peerInfoVector.addElement(new RemotePeerInfo(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]),
						 Integer.parseInt(tokens[3]) == 1,i));
				 i++;
			}
			in.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * Checks if all processes has finished
	 */
	public synchronized  boolean allDone(){
		boolean isfinished = true;
		try {
			for(Process x: peerProcesses){
				System.out.println("process " + x.pid() + " in state: " + x.waitFor());
				if(x.waitFor() == 0){
					System.out.println("process " + x.pid() + "execute properly");
				}
				else if(x.waitFor() == 255){
					InputStream error = x.getErrorStream();
					String s = new String(error.readAllBytes(), StandardCharsets.UTF_8);
					System.out.println("SSH fail, " + s);
					isfinished = false;
					break;
				}
				else {
					isfinished = false;
					System.out.println("Unknown error");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return isfinished;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean shouldStop = false;
		boolean isCheckingstop = true;
		try {
			StartRemotePeers start = new StartRemotePeers();
			start.getConfiguration();
					
			// get current path
			String path = System.getProperty("user.dir");
			
			// start clients at remote hosts
			for (int i = 0; i < start.peerInfoVector.size(); i++) {
				RemotePeerInfo pInfo = (RemotePeerInfo) start.peerInfoVector.elementAt(i);
				System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				String command = "ssh " + pInfo.peerAddress + " cd " + path + "; java peerProcess " + pInfo.peerId;
				start.peerProcesses.add(Runtime.getRuntime().exec(command));
				System.out.println(command);
			}

			System.out.println("Waiting for remote peers to terminate.." );
			boolean isdone = start.allDone();
			while(!isdone){
				Thread.sleep(5000);
				isdone = start.allDone();
			}
			System.out.println("All processes finished");
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
