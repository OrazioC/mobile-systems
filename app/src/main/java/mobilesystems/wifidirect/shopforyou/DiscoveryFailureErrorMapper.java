package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.IntRange;

class DiscoveryFailureErrorMapper {
    public String map(@IntRange(from = 0, to = 3) int reason) {
        switch (reason) {
            case 0:
                return "Discovery operation failed due to an internal error";
            case 1:
                return "Discovery operation failed because p2p is not supported on the device";
            case 2:
                return "Discovery operation failed because the framework is busy and unable to service the request (Tip: WiFi might be off)";
            case 3:
                return "Discovery service failed because no service requests were added";
            default:
                // Fail fast
                throw new IllegalArgumentException("Error code not supported");
        }
    }
}
