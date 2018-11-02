package mobilesystems.wifidirect.shopforyou.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

public class WiFi2P2BroadcastReceiver extends BroadcastReceiver {

    private static final @NonNull String TAG = "WIFI_P2P";

    /**
     * adb shell am broadcast -a android.net.wifi.p2p.STATE_CHANGED --ei wifi_p2p_state 1
     * <p>
     * 1 -> WIFI_P2P_STATE_DISABLED
     * 2 -> WIFI_P2P_STATE_ENABLED
     * <p>
     * adb shell am broadcast -a android.net.wifi.p2p.PEERS_CHANGED
     * <p>
     * adb shell am broadcast -a android.net.wifi.p2p.CONNECTION_STATE_CHANGE
     * <p>
     * adb shell am broadcast -a android.net.wifi.p2p.THIS_DEVICE_CHANGED
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /*
         * Intent is received when the state of the Wi-Fi p2p has changed.
         */
        if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "received intent: " + WIFI_P2P_STATE_CHANGED_ACTION);
            /*
             * Indicates whether Wi-Fi p2p is enabled or disabled
             */
            int wifiP2PState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);

            Log.d(TAG, "WiFi P2P status: " + wifiP2PState);
        } else
            /*
             * Intent is received when the peer list has changed.
             * It can happen when a peer is found, lost or updated
             */
            if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "received intent: " + WIFI_P2P_PEERS_CHANGED_ACTION);
                //TODO we might want the P2P manager to request the list of peers
            } else
                /*
                 * //TODO Add comment
                 */
                if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(intent)) {
                    Log.d(TAG, "received intent: " + WIFI_P2P_CONNECTION_CHANGED_ACTION);
                } else
                    /*
                     * //TODO Add comment
                     */
                    if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(intent)) {
                        Log.d(TAG, "received intent: " + WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
                    }
    }
}
