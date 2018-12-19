package mobilesystems.wifidirect.shopforyou.peerlist;

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
            case 0:
                return CONNECTION_STATUS_CONNECTED;
            case 1:
                return CONNECTION_STATUS_INVITED;
            case 2:
                return CONNECTION_STATUS_FAILED;
            case 3:
                return CONNECTION_STATUS_AVAILABLE;
            case 4:
                return CONNECTION_STATUS_UNAVAILABLE;
            default:
                throw new IllegalArgumentException("Connection status not defined");
        }
    }
}
