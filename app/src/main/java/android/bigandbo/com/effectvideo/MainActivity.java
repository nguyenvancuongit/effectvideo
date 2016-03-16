package android.bigandbo.com.effectvideo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "EFFECTVIDEO";
    public static final String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Failed on load FFmpeg library");
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
            String input = ExternalStorageDirectoryPath + "/Android/input.mp4";
            String output = ExternalStorageDirectoryPath + "/Android/output.mp4";
            String musicPath = ExternalStorageDirectoryPath + "/Android/CoLeTaNenDungLai.mp3";
//            String cmd = "-i " + input + " -threads 10 -preset ultrafast -vf setpts=2*PTS -an " + output;
//            String cmd = "-i " + input + " -threads 10 -preset ultrafast -vf setpts=0.5*PTS -an " + output;
            String cmd = "-i " + musicPath + " -i " + input + " -codec copy -shortest " + output;
            Log.e(TAG, "command " + cmd);
            Log.e(TAG, "getLibraryFFmpegVersion " + ffmpeg.getLibraryFFmpegVersion());
            Log.e(TAG, "getDeviceFFmpegVersion " + ffmpeg.getDeviceFFmpegVersion());
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress on executing FFmpeg command " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "Failed on executing FFmpeg command");
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "Success on executing FFmpeg command");
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
            ffmpeg.killRunningProcesses();
        }
    }

    class FFmpegExecuteAsyncTask extends AsyncTask<Void, String, NewCommandResult> {
        private final String cmd;
        private final FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler;
        private final NewShellCommand shellCommand;
        private final long timeout;
        private long startTime;
        private Process process;
        private String output = "";

        FFmpegExecuteAsyncTask(String cmd, long timeout, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
            this.cmd = cmd;
            this.timeout = timeout;
            this.ffmpegExecuteResponseHandler = ffmpegExecuteResponseHandler;
            this.shellCommand = new NewShellCommand();
        }

        protected void onPreExecute() {
            this.startTime = System.currentTimeMillis();
            if (this.ffmpegExecuteResponseHandler != null) {
                this.ffmpegExecuteResponseHandler.onStart();
            }

        }

        protected NewCommandResult doInBackground(Void... params) {
            NewCommandResult e;
            try {
                this.process = this.shellCommand.run(this.cmd);
                if (this.process == null) {
                    e = NewCommandResult.getDummyFailureResponse();
                    return e;
                }

                this.checkAndUpdateProcess();
                e = NewCommandResult.getOutputFromProcess(this.process);
            } catch (TimeoutException var8) {
                NewCommandResult var3 = new NewCommandResult(false, var8.getMessage());
                return var3;
            } catch (Exception var9) {

                return NewCommandResult.getDummyFailureResponse();
            } finally {
                Util.destroyProcess(this.process);
            }

            return e;
        }

        protected void onProgressUpdate(String... values) {
            if (values != null && values[0] != null && this.ffmpegExecuteResponseHandler != null) {
                this.ffmpegExecuteResponseHandler.onProgress(values[0]);
            }

        }

        protected void onPostExecute(NewCommandResult commandResult) {
            if (this.ffmpegExecuteResponseHandler != null) {
                this.output = this.output + commandResult.output;
                if (commandResult.success) {
                    this.ffmpegExecuteResponseHandler.onSuccess(this.output);
                } else {
                    this.ffmpegExecuteResponseHandler.onFailure(this.output);
                }

                this.ffmpegExecuteResponseHandler.onFinish();
            }

        }

        private void checkAndUpdateProcess() throws TimeoutException, InterruptedException {
            while (!Util.isProcessCompleted(this.process)) {
                if (Util.isProcessCompleted(this.process)) {
                    return;
                }

                if (this.timeout != 9223372036854775807L && System.currentTimeMillis() > this.startTime + this.timeout) {
                    throw new TimeoutException("FFmpeg timed out");
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));

                    String e;
                    while ((e = reader.readLine()) != null) {
                        this.output = this.output + e + "\n";
                        this.publishProgress(new String[]{e});
                    }
                } catch (IOException var3) {
                    var3.printStackTrace();
                }
            }

        }

        boolean isProcessCompleted() {
            return Util.isProcessCompleted(this.process);
        }
    }
}
