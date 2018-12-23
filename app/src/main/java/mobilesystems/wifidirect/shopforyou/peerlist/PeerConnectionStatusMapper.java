package mobilesystems.wifidirect.shopforyou.peerlist;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class PeerConnectionStatusMapper {

    static final @NonNull String CONNECTION_STATUS_CONNECTED = "CONNECTED";
    private static final @NonNull String CONNECTION_STATUS_INVITED = "INVITED";
    private static final @NonNull String CONNECTION_STATUS_FAILED = "FAILED";
    private static final @NonNull String CONNECTION_STATUS_AVAILABLE = "AVAILABLE";
    private static final @NonNull String CONNECTION_STATUS_UNAVAILABLE = "UNAVAILABLE";

    public @NonNull String map (@IntRange(from = 0, to = 4) int status) {
        switch (status) {
            case WifiP2pDevice.CONNECTED:
                return CONNECTION_STATUS_CONNECTED;
            case WifiP2pDevice.INVITED:
                return CONNECTION_STATUS_INVITED;
            case WifiP2pDevice.FAILED:
                return CONNECTION_STATUS_FAILED;
            case WifiP2pDevice.AVAILABLE:
                return CONNECTION_STATUS_AVAILABLE;
            case WifiP2pDevice.UNAVAILABLE:
                return CONNECTION_STATUS_UNAVAILABLE;
            default:
                throw new IllegalArgumentException("Connection status not defined");
        }
    }
}
