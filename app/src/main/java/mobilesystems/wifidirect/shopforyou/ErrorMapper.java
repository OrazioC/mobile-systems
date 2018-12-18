package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.IntRange;

class ErrorMapper {
    public String map(@IntRange(from = 0, to = 3) int reason) {
        switch (reason) {
            case 0:
                return "Operation failed due to an internal error";
            case 1:
                return "Operation failed because p2p is not supported on the device";
            case 2:
                return "Operation failed because the framework is busy and unable to service the request (Tip: WiFi might be off)";
            case 3:
                return "Service failed because no service requests were added";
            default:
                // Fail fast
                throw new IllegalArgumentException("Error code not supported");
        }
    }
}
