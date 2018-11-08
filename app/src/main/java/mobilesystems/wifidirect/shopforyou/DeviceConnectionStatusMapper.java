package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class DeviceConnectionStatusMapper {

    public @NonNull String map (@IntRange(from = 0, to = 4) int status) {
        switch (status) {
            case 0:
                return "CONNECTED";
            case 1:
                return "INVITED";
            case 2:
                return "FAILED";
            case 3:
                return "AVAILABLE";
            case 4:
                return "UNAVAILABLE";
            default:
                throw new IllegalArgumentException("Connection status not defined");
        }
    }
}
