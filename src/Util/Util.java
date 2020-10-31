package Util;

import java.nio.charset.StandardCharsets;

public class Util {

    public static String convertByteToString(byte[] content){
        return new String(content, StandardCharsets.UTF_8);
    }
}
