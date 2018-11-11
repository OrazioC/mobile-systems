package mobilesystems.wifidirect.shopforyou;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

public class InfoTransferService extends IntentService {

    private final static String ACTION_SEND_MESSAGE = "ACTION_SEND_MESSAGE";
    private final static String EXTRAS_GROUP_OWNER_ADDRESS = "EXTRAS_GROUP_OWNER_ADDRESS";
    private final static String EXTRAS_GROUP_OWNER_PORT = "EXTRAS_GROUP_OWNER_PORT";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public InfoTransferService(String name) {
        super(name);
    }

    public InfoTransferService() {
        super("InfoTransferService");
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String ownerAddress) {
        Intent serviceIntent = new Intent(context, InfoTransferService.class);
        serviceIntent.setAction(ACTION_SEND_MESSAGE);
        serviceIntent.putExtra(EXTRAS_GROUP_OWNER_ADDRESS, ownerAddress);
        serviceIntent.putExtra(EXTRAS_GROUP_OWNER_PORT, 8988);
        return serviceIntent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("Transfer service", "onHandleIntent");
        assert intent != null;
        String host = requireNonNull(intent.getExtras()).getString(EXTRAS_GROUP_OWNER_ADDRESS);
        int port = requireNonNull(intent.getExtras()).getInt(EXTRAS_GROUP_OWNER_PORT);

        if (requireNonNull(intent.getAction()).equals(ACTION_SEND_MESSAGE)) {
            Socket socket = new Socket();
            try {
                socket.bind(null);
                Log.d("Transfer Service", "Connecting to host: " + host + ", port: " + port);
                socket.connect((new InetSocketAddress(host, port)), 5000);
                Log.d("Transfer Service", "Client socket - " + socket.isConnected());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.append("dummy message -> welcome to the other side");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //Silently fail
                    }
                }
            }
        }
    }
}
