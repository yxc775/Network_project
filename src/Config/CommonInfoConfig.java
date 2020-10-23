package Config;

import java.io.BufferedReader;
import java.io.FileReader;

public class CommonInfoConfig {
    //Read PeerInfo file and store it in Config object;
    public static void readCommonInfo(String filename){
        String st;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while((st = in.readLine()) != null) {
                String[] tokens = st.split("\\s+");
                switch (tokens[0]){
                    case "NumberOfPreferredNeighbors":
                        CommonAttributes.numberOfPreferedN = Integer.parseInt(tokens[1]);
                        break;
                    case "UnchokingInterval":
                        CommonAttributes.unChokeInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "OptimisticUnchokingInterval":
                        CommonAttributes.optimisticUnchokeInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "FileName":
                        CommonAttributes.filename = tokens[1];
                        break;
                    case "FileSize":
                        CommonAttributes.filesize = Integer.parseInt(tokens[1]);
                        break;
                    case "PieceSize":
                        CommonAttributes.piecesize = Integer.parseInt(tokens[1]);
                        break;
                    default:
                        break;
                }
            }

            in.close();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    //This code is test read common info
    public static void main(String[] arg){
        readCommonInfo("Common.cfg");
    }

}
