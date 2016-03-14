package android.bigandbo.com.effectvideo;

import java.io.IOException;

/**
 * Created by hieunt on 9/1/2015.
 */
public class NewShellCommand {
    NewShellCommand() {
    }

    Process run(String commandString) {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(commandString);
        } catch (IOException var4) {
        }

        return process;
    }

    NewCommandResult runWaitFor(String s) {
        Process process = this.run(s);
        Integer exitValue = null;
        String output = null;

        try {
            if (process != null) {
                exitValue = Integer.valueOf(process.waitFor());
                if (NewCommandResult.success(exitValue)) {
                    output = Util.convertInputStreamToString(process.getInputStream());
                } else {
                    output = Util.convertInputStreamToString(process.getErrorStream());
                }
            }
        } catch (InterruptedException var9) {

        } finally {
            Util.destroyProcess(process);
        }

        return new NewCommandResult(NewCommandResult.success(exitValue), output);
    }
}
