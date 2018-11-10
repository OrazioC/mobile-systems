package mobilesystems.wifidirect.shopforyou.peerlist;

import android.support.annotation.NonNull;

import java.util.List;

public class PeerListAdapterPresenter implements PeerListAdapterContract.Presenter {

    private final PeerListAdapterContract.View adapter;

    public PeerListAdapterPresenter(PeerListAdapterContract.View adapter) {
        this.adapter = adapter;
    }

    @Override
    public void populateList(@NonNull List<PeerModel> peerList) {
        adapter.setPeerList(peerList);
    }

    @Override
    public void setListener(@NonNull PeerListener listener) {
        adapter.setListener(listener);
    }
}
