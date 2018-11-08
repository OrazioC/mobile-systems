package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
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
    private @NonNull DeviceConnectionStatusMapper deviceConnectionStatusMapper;
    private @NonNull DiscoveryFailureErrorMapper errorMapper;

    private @NonNull List<WifiP2pDevice> devices = new ArrayList<>();

    public HomeFragmentPresenter(@NonNull HomeFragmentContract.View view,
                                 @NonNull PeerListAdapter adapter,
                                 @NonNull WifiP2pManager manager,
                                 @NonNull WifiP2pManager.Channel channel) {
        this.view = view;
        this.listAdapterPresenter = new PeerListAdapterPresenter(adapter);
        this.manager = manager;
        this.channel = channel;
        this.deviceConnectionStatusMapper = new DeviceConnectionStatusMapper();
        this.errorMapper = new DiscoveryFailureErrorMapper();
    }

    @Override
    public void startDiscovery() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Discovery Initiated");
            }

            @Override
            public void onFailure(int reason) {
                String errorMessage = errorMapper.map(reason);
                view.displayError(errorMessage);
            }
        });
    }

    @Override
    public void populateList() {
        manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                devices.clear();
                devices.addAll(wifiP2pDeviceList.getDeviceList());
                List<PeerModel> peerList = new ArrayList<>();
                for (final WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    peerList.add(new PeerModel(device.deviceName, device.deviceAddress, device.primaryDeviceType, deviceConnectionStatusMapper.map(device.status), new Runnable() {
                        @Override
                        public void run() {
                            connect(device.deviceAddress);
                        }
                    }));
                }
                listAdapterPresenter.populateList(peerList);
            }
        });
    }

    @Override
    public void showWiFiStatus(boolean isWiFiEnabled) {
        view.displayWiFiStatus(isWiFiEnabled ? "WiFi P2P is enabled" : "WiFi P2P is disable");
    }

    private void connect(@NonNull String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        /*
         * registers for WIFI_P2P_CONNECTION_CHANGED_ACTION
         */
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Connection successful");
            }

            @Override
            public void onFailure(int reason) {
                view.displayError(errorMapper.map(reason));
            }
        });
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
