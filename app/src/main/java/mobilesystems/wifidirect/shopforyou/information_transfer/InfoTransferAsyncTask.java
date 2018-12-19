package mobilesystems.wifidirect.shopforyou.information_transfer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import mobilesystems.wifidirect.shopforyou.HomeFragmentContract;

public class InfoTransferAsyncTask extends AsyncTask<Void, Void, String> {

    private static final @NonNull String TAG = "MOBILE_SYSTEM_AT";

    private @NonNull HomeFragmentContract.Presenter presenter;

    public InfoTransferAsyncTask(@NonNull HomeFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    //Runs on background thread
    @Override
    protected String doInBackground(Void... params) {

        try {
            ServerSocket serverSocket = new ServerSocket(8988);

            Log.d(TAG, "Server: socket opened");
            // needs android.permission.INTERNET
            Socket client = serverSocket.accept();
            Log.d(TAG, "Server: connection made");

            BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder message = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                message.append(line).append('\n');
            }

            return message.toString();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    //Runs on UI thread
    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        if (message != null && !message.isEmpty()) {
            String[] item = message.split("\t");
            presenter.saveMessage(item[0], item[1]);
            presenter.showMessage(item[0], item[1]);
        }
    }
}
