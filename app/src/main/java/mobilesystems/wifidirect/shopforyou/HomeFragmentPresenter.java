package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;

    public HomeFragmentPresenter(HomeFragmentContract.View view, @NonNull PeerListAdapter adapter) {
        this.view = view;
        listAdapterPresenter = new PeerListAdapterPresenter(adapter);
    }

    @Override
    public void populateList() {
        //TODO this should the list of available peers coming back from the discovery phase
        List<String> peerList = Arrays.asList("A", "B", "C");
        listAdapterPresenter.populateList(peerList);
    }

    @Override
    public void showWiFiStatus(boolean isWiFiEnabled) {
        view.displayWiFiStatus(isWiFiEnabled ? "WiFi P2P is enabled" : "WiFi P2P is disable");
    }
}
