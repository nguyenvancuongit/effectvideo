package android.bigandbo.com.effectvideo;

/**
 * Created by hieunt on 9/1/2015.
 */
public class NewCommandResult {
    final String output;
    final boolean success;

    NewCommandResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    static NewCommandResult getDummyFailureResponse() {
        return new NewCommandResult(false, "");
    }

    static NewCommandResult getOutputFromProcess(Process process) {
        String output;
        if (success(Integer.valueOf(process.exitValue()))) {
            output = Util.convertInputStreamToString(process.getInputStream());
        } else {
            output = Util.convertInputStreamToString(process.getErrorStream());
        }

        return new NewCommandResult(success(Integer.valueOf(process.exitValue())), output);
    }

    static boolean success(Integer exitValue) {
        return exitValue != null && exitValue.intValue() == 0;
    }
}
