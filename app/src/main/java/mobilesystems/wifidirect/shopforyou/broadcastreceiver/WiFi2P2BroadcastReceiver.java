package mobilesystems.wifidirect.shopforyou.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.util.Log;

import mobilesystems.wifidirect.shopforyou.HomeFragmentContract;

import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;
import static java.util.Objects.requireNonNull;

public class WiFi2P2BroadcastReceiver extends BroadcastReceiver {

    private static final @NonNull
    String TAG = "MOBILE_SYSTEM_BR";

    private final @NonNull
    HomeFragmentContract.Presenter homePresenter;

    public WiFi2P2BroadcastReceiver(@NonNull HomeFragmentContract.Presenter homePresenter) {
        this.homePresenter = homePresenter;
    }

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
        /*
         * Code runs n UI thread, avoid long running jobs
         */

        String action = intent.getAction();



        switch (requireNonNull(action)) {
            /*
             * Intent is received when the state of the Wi-Fi p2p has changed.
             */
            case WIFI_P2P_STATE_CHANGED_ACTION:
                Log.d(TAG, "received intent: " + WIFI_P2P_STATE_CHANGED_ACTION);
                /*
                 * Indicates whether Wi-Fi p2p is enabled or disabled
                 */
                int wifiP2PState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);
                Log.d(TAG, "WiFi P2P status enabled: " + wifiP2PState);
                break;
            /*
             * Intent is received when the peer list has changed.
             * It can happen when a peer is found, lost or updated
             */
            case WIFI_P2P_PEERS_CHANGED_ACTION:
                //Ignoring this intent as the list of peers is handled in the Service Request Callback
                Log.d(TAG, "received intent: " + WIFI_P2P_PEERS_CHANGED_ACTION + " contains the list of peers available");
                break;
            case WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Log.d(TAG, "received intent: " + WIFI_P2P_CONNECTION_CHANGED_ACTION);

                WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                Log.d(TAG, "wifiDirectGroup: " + wifiP2pGroup);
                WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                Log.d(TAG, "wifiDirectInfo: " + wifiP2pInfo);

                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                Log.d(TAG, "networkInfo: " + networkInfo);
                if (networkInfo.isConnected()) {
                    homePresenter.requestDeviceConnectionInfo();
                }
                break;
            case WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                Log.d(TAG, "received intent: " + WIFI_P2P_THIS_DEVICE_CHANGED_ACTION + "\n" + device);
                break;
            case WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                Log.d(TAG, "received intent: " + WIFI_P2P_DISCOVERY_CHANGED_ACTION);
                Integer discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                if (discoveryState == WIFI_P2P_DISCOVERY_STOPPED) {
                    Log.d(TAG, "discovery stopped");
                } else if (discoveryState == WIFI_P2P_DISCOVERY_STARTED) {
                    Log.d(TAG, "discovery started");
                } else {
                    Log.d(TAG, "discovery status unknown");
                }
                break;
            default:
                //
                break;
        }


//        /*
//         * Intent is received when the state of the Wi-Fi p2p has changed.
//         */
//        if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
//            Log.d(TAG, "received intent: " + WIFI_P2P_STATE_CHANGED_ACTION);
//            /*
//             * Indicates whether Wi-Fi p2p is enabled or disabled
//             */
//            int wifiP2PState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);
//            Log.d(TAG, "WiFi P2P status: " + wifiP2PState);
//        } else
//            /*
//             * Intent is received when the peer list has changed.
//             * It can happen when a peer is found, lost or updated
//             */
//            if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
//
//            } else
//                /*
//                 * //TODO Add comment
//                 */
//                if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
//                    Log.d(TAG, "received intent: " + WIFI_P2P_CONNECTION_CHANGED_ACTION);
//                    // TODO add code to notify update status for the connected device
//                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//                    if (networkInfo.isConnected()) {
//                        homePresenter.requestDeviceConnectionInfo();
//                    }
//                } else
//                    /*
//                     * //TODO Add comment
//                     */
//                    if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//                        WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
//                        Log.d(TAG, "received intent: " + WIFI_P2P_THIS_DEVICE_CHANGED_ACTION + "\n" + device);
//                    }
    }
}

