package mobilesystems.wifidirect.shopforyou.peerlist;

import android.support.annotation.NonNull;

import java.util.List;

public interface PeerListAdapterContract {
    interface View {

        void setPeerList(@NonNull List<PeerModel> peerList);

        void setListener(PeerListener listener);
    }
    interface Presenter {

        void populateList(@NonNull List<PeerModel> peerList);

        void setListener(@NonNull PeerListener listener);
    }
}
