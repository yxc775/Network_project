package Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
    static FileOutputStream logfile;
    static OutputStreamWriter output;

    public static void start(String file) throws IOException {
        logfile = new FileOutputStream(file);
        output = new OutputStreamWriter(logfile, "UTF-8");
    }

    // Restore the original settings.
    public static void stop() {
        try {
            output.flush();
            logfile.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void writeLog(String str) {
        try {
            output.write(str + '\n');
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}

