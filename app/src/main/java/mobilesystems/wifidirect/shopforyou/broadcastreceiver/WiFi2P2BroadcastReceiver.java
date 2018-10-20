package mobilesystems.wifidirect.shopforyou.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

public class WiFi2P2BroadcastReceiver extends BroadcastReceiver{

    private static final @NonNull String TAG = "WIFI_P2P";

    /**
     * adb shell am broadcast -a android.net.wifi.p2p.STATE_CHANGED --ei wifi_p2p_state 1
     *
     * 1 -> WIFI_P2P_STATE_DISABLED
     * 2 -> WIFI_P2P_STATE_ENABLED
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /*
         * Intent is received when the state of the Wi-Fi p2p has changed.
         */
        if(WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            /*
             * Indicates whether Wi-Fi p2p is enabled or disabled
             */
            int wifiP2PState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);

            Log.d(TAG, "WiFi P2P status: " + wifiP2PState);
        }
    }
}
