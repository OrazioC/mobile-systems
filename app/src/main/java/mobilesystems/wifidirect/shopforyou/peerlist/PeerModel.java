package mobilesystems.wifidirect.shopforyou.peerlist;

import android.support.annotation.NonNull;

public class PeerModel {
    public final @NonNull String name;
    public final @NonNull String address;
    public final @NonNull String type;
    public final @NonNull String status;

    PeerModel(@NonNull String name,
              @NonNull String address,
              @NonNull String type,
              @NonNull String status) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.status = status;
    }
}
