package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

import java.util.List;

public interface PeerListAdapterContract {
    interface View {

        void setPeerList(@NonNull List<PeerModel> peerList);
    }
    interface Presenter {

        void populateList(@NonNull List<PeerModel> peerList);
    }
}
