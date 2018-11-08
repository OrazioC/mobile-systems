package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

class PeerModel {
    public final @NonNull String name;
    public final @NonNull String address;
    public final @NonNull String type;
    public final @NonNull String status;
    public final @NonNull Runnable connectTask;

    PeerModel(@NonNull String name,
              @NonNull String address,
              @NonNull String type,
              @NonNull String status,
              @NonNull Runnable connectTask) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.status = status;
        this.connectTask = connectTask;
    }
}
