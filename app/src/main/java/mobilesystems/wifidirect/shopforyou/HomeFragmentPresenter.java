package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private @NonNull WifiP2pManager manager;
    private @NonNull WifiP2pManager.Channel channel;
    private @NonNull DiscoveryFailureErrorMapper errorMapper;

    public HomeFragmentPresenter(@NonNull HomeFragmentContract.View view,
                                 @NonNull PeerListAdapter adapter,
                                 @NonNull WifiP2pManager manager,
                                 @NonNull WifiP2pManager.Channel channel) {
        this.view = view;
        this.listAdapterPresenter = new PeerListAdapterPresenter(adapter);
        this.manager = manager;
        this.channel = channel;
        this.errorMapper = new DiscoveryFailureErrorMapper();
    }

    @Override
    public void startDiscovery() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayDiscoveryInitiated("Discovery Initiated");
            }

            @Override
            public void onFailure(int reason) {
                String errorMessage = errorMapper.map(reason);
                view.displayDiscoveryFailure(errorMessage);
            }
        });
    }

    @Override
    public void populateList() {
        manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                List<String> peerList = new ArrayList<>();
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    peerList.add(device.deviceName + " " + device.deviceAddress + " " + device.primaryDeviceType + " " + device.status);
                }
                listAdapterPresenter.populateList(peerList);
            }
        });
    }

    @Override
    public void showWiFiStatus(boolean isWiFiEnabled) {
        view.displayWiFiStatus(isWiFiEnabled ? "WiFi P2P is enabled" : "WiFi P2P is disable");
    }


    // WifiP2pManager.PeerListListener
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }

    // WifiP2pManager.ConnectionInfoListener
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
