package mobilesystems.wifidirect.shopforyou;

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
}
