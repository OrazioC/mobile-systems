package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {

    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;

    public HomeFragmentPresenter(@NonNull PeerListAdapter adapter) {
        listAdapterPresenter = new PeerListAdapterPresenter(adapter);
    }

    @Override
    public void populateList() {
        //TODO this should the list of available peers coming back from the discovery phase
        List<String> peerList = Arrays.asList("A", "B", "C");
        listAdapterPresenter.populateList(peerList);
    }
}
