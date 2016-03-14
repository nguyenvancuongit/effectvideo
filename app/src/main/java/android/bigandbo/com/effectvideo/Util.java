package android.bigandbo.com.effectvideo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by hieunt on 9/1/2015.
 */
public class Util {
    static boolean isDebug(Context context) {
        ApplicationInfo var10001 = context.getApplicationContext().getApplicationInfo();
        return 0 != (var10001.flags &= 2);
    }

    static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    static String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader e = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String str;
            while ((str = e.readLine()) != null) {
                sb.append(str);
            }

            return sb.toString();
        } catch (IOException var4) {
            return null;
        }
    }

    static void destroyProcess(Process process) {
        if (process != null) {
            process.destroy();
        }

    }

    static boolean killAsync(AsyncTask asyncTask) {
        return asyncTask != null && !asyncTask.isCancelled() && asyncTask.cancel(true);
    }

    static boolean isProcessCompleted(Process process) {
        try {
            if (process == null) {
                return true;
            } else {
                process.exitValue();
                return true;
            }
        } catch (IllegalThreadStateException var2) {
            return false;
        }
    }
}
