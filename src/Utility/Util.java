package Utility;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Util {

    public static String convertByteToString(byte[] content){
        return new String(content, StandardCharsets.UTF_8);
    }
    public static int convertByteToInt(byte[] content){
        return ByteBuffer.wrap(content).getInt();
    }
    public static byte[] convertInttoFourByte(int x){
        return ByteBuffer.allocate(4).putInt(x).array();
    }

    //this will call log generator to create loc based on input string
    public static void PrintLog(String message) {

    }
}
