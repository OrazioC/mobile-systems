package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterContract;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterPresenter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListener;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModel;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private @NonNull WifiP2pManager manager;
    private @NonNull WifiP2pManager.Channel channel;
    private @NonNull DeviceConnectionStatusMapper deviceConnectionStatusMapper;
    private @NonNull DiscoveryFailureErrorMapper errorMapper;

    private @NonNull List<WifiP2pInfo> groupPeerInfoList = new ArrayList<>();

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
    public void init() {
        listAdapterPresenter.setListener(new PeerListener(){
            @Override
            public void onClick(@NonNull PeerModel model) {
                connect(model.address);
            }
        });
    }

    @Override
    public void showWiFiStatus(boolean isWiFiEnabled) {
        view.displayWiFiStatus(isWiFiEnabled ? "WiFi P2P is enabled" : "WiFi P2P is disable");
    }

    //region WifiP2PManager
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
    public void populatePeerList() {
        // WifiP2pManager.PeerListListener
        manager.requestPeers(channel, this);
    }

    @Override
    public void requestDeviceConnectionInfo() {
        // WifiP2pManager.ConnectionInfoListener
        manager.requestConnectionInfo(channel, this);
    }

    @Override
    public void displayMessage(@NonNull String message) {
        view.displayMessageFromOtherPeer(message);
    }

    @Override
    public void sendMessageToConnectedPeer() {
        view.startTransferService(groupPeerInfoList.get(0).groupOwnerAddress.getHostAddress());
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
    //endregion

    //region WifiP2pManager.PeerListListener
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        List<PeerModel> peerList = new ArrayList<>();
        for (final WifiP2pDevice device : peers.getDeviceList()) {
            peerList.add(new PeerModel(device.deviceName, device.deviceAddress, device.primaryDeviceType, deviceConnectionStatusMapper.map(device.status)));
        }
        listAdapterPresenter.populateList(peerList);
    }
    //endregion

    //region WifiP2pManager.ConnectionInfoListener
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        //TODO we need to clear this list as it might cause unwanted behaviors. Like trying to connect to an old IP or similar?
        this.groupPeerInfoList.add(info);

        view.displayDeviceInfo(
                info.isGroupOwner ? "The device is the group owner" : "device is part of a group",
                info.groupOwnerAddress.toString()
        );

        if (info.groupFormed && info.isGroupOwner) {
            new InfoTransferAsyncTask(this).execute();
        }
    }
    //endregion


}
