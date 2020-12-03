package Utility;
import Logger.Logger;
import MessageObjects.Message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        Logger.writeLog(getTime() + ": Peer " + message);
    }

    public static String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static byte[] encodeMessageWithPayload(Message x){
        byte[] msg = new byte[x.getMessageLength().length + 1 + x.getPayload().length];
        byte[] type = new byte[1];
        type[0] = x.getMessageType();
        System.arraycopy(x.getMessageLength(), 0, msg, 0,
                x.getMessageLength().length);
        System.arraycopy(type, 0, msg, x.getMessageLength().length, 1);
        System.arraycopy(x.getPayload(), 0, msg, x.getMessageLength().length + 1, x.getPayload().length);

        return msg;
    }

    public static byte[] encodeMessageWithOutPayload(Message x){
        byte[] msg = new byte[x.getMessageLength().length + 1];
        byte[] type = new byte[1];
        type[0] = x.getMessageType();
        System.arraycopy(x.getMessageLength(), 0, msg, 0, x.getMessageLength().length);
        System.arraycopy(type, 0, msg, x.getMessageLength().length, 1);
        return msg;
    }

    public static byte[] intToByteArray(int value)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }


    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }




    static String byteArrayToHexString(byte in[])
    {
        byte ch = 0x00;

        int i = 0;

        if (in == null || in.length <= 0)
            return null;
        String hexDigits[] = {"0", "1", "2","3", "4", "5", "6", "7", "8","9", "A", "B", "C", "D", "E","F"};

        StringBuffer out = new StringBuffer(in.length * 2);

        for (i = 0; i < in.length; i++)
        {
            ch = (byte) (in[i] & 0xF0);
            ch = (byte) (ch >>> 4);
            // shift the bits down

            ch = (byte) (ch & 0x0F);
            // must do this is high order bit is on!

            out.append(hexDigits[ (int) ch]);

            ch = (byte) (in[i] & 0x0F);

            out.append(hexDigits[ (int) ch]);

        }

        String rslt = new String(out);

        return rslt;
    }
    public static int byteArrayToInt(byte[] b, int offset)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }


}
