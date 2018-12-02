package mobilesystems.wifidirect.shopforyou;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class InfoTransferAsyncTask extends AsyncTask<Void, Void, String> {

    private @NonNull HomeFragmentContract.Presenter presenter;

    public InfoTransferAsyncTask(@NonNull HomeFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    //Runs on background thread
    @Override
    protected String doInBackground(Void... params) {

        try {
            ServerSocket serverSocket = new ServerSocket(8988);

            Log.d("Transfer Async Task", "Server: socket opened");
            // needs android.permission.INTERNET
            Socket client = serverSocket.accept();
            Log.d("Transfer Async Task", "Server: connection made");

            BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder message = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                message.append(line).append('\n');
            }
            String[] item = message.toString().split("\t");
            presenter.saveMessage(item[0], item[1]);

            return message.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Runs on UI thread
    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
    }
}
