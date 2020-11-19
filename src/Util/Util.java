package Util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Util {

    public static String convertByteToString(byte[] content){
        return new String(content, StandardCharsets.UTF_8);
    }
    public static int convertByteToInt(byte[] content){
        return ByteBuffer.wrap(content).getInt();
    }
}
