package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import mobilesystems.wifidirect.shopforyou.database.AppDatabase;
import mobilesystems.wifidirect.shopforyou.database.ItemEntity;
import mobilesystems.wifidirect.shopforyou.information_transfer.InfoTransferAsyncTask;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerConnectionStatusMapper;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterContract;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterPresenter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListener;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModel;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModelMapper;
import mobilesystems.wifidirect.shopforyou.upnp.UpnpServiceAdvertiser;
import mobilesystems.wifidirect.shopforyou.upnp.UpnpServiceDiscovery;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter, WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "MOBILE_SYSTEM";
    private static final String SERVICE_INSTANCE = "_mobilesystem";

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private @NonNull WifiP2pManager manager;
    private @NonNull WifiP2pManager.Channel channel;
    private @NonNull PeerModelMapper modelMapper;
    private @NonNull ErrorMapper errorMapper;
    private @NonNull AppDatabase database;
    private @NonNull UpnpServiceDiscovery upnpServiceDiscovery;
    private @NonNull UpnpServiceAdvertiser advertiser;

    private @NonNull List<WifiP2pInfo> groupPeerInfoList = new ArrayList<>();

    HomeFragmentPresenter(@NonNull HomeFragmentContract.View view,
                          @NonNull PeerListAdapter adapter,
                          @NonNull WifiP2pManager manager,
                          @NonNull WifiP2pManager.Channel channel,
                          @NonNull AppDatabase database) {
        this.view = view;
        this.listAdapterPresenter = new PeerListAdapterPresenter(adapter);
        this.manager = manager;
        this.channel = channel;
        this.modelMapper = new PeerModelMapper(new PeerConnectionStatusMapper());
        this.errorMapper = new ErrorMapper();
        this.database = database;

        upnpServiceDiscovery = new UpnpServiceDiscovery(this, manager, channel, errorMapper, listAdapterPresenter, modelMapper);
        advertiser = new UpnpServiceAdvertiser(this, manager, channel, errorMapper);
    }

    @Override
    public void init() {
        listAdapterPresenter.setListener(new PeerListener() {
            @Override
            public void onClick(@NonNull PeerModel model) {
                connect(model.address);
            }
        });
    }

    @Override
    public void display(@NonNull String message) {
        view.displayMessage(message);
    }

    //region Register Service
    @Override
    public void register() {
        advertiser.startAdvertise();
    }
    //endregion

    //region Discover Service
    @Override
    public void discover() {
        upnpServiceDiscovery.startDiscovery();
    }
    //endregion

    //region Release Resources
    @Override
    public void unregisterServiceRequest() {
        upnpServiceDiscovery.unregisterServiceRequest();
    }

    @Override
    public void stopDiscovery() {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayMessage("Service discovery stopped");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                view.displayMessage("Service discovery stop failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }

    @Override
    public void cancelAnyOngoingGroupNegotiation() {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Cancelled any ongoing group negotiation");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                Log.d(TAG, "Cancel ongoing group negotiation failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }

    @Override
    public void destroyGroup() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                view.displayMessage("Group removed");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                view.displayMessage("Group removal failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }
    //endregion

    //region Connection handler
    private void connect(@NonNull String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;

        upnpServiceDiscovery.unregisterServiceRequest();
        /*
         * registers for WIFI_P2P_CONNECTION_CHANGED_ACTION
         */
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayMessage("Connection requested");
            }

            @Override
            public void onFailure(int reasonCode) {
                view.displayMessage("Group removal failed" + errorMapper.map(reasonCode));
            }
        });
    }
    //endregion

    //region WifiP2PManager.ConnectionInfoListener
    @Override
    public void requestDeviceConnectionInfo() {
        manager.requestConnectionInfo(channel, this);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        groupPeerInfoList.add(info);

        view.displayDeviceInfo(
                info.isGroupOwner
                        ? "The device is the Group Owner"
                        : "The device is a P2P Client"
        );

        if (info.groupFormed) {
            if (info.isGroupOwner) {
                new InfoTransferAsyncTask(this).execute();
            } else {
                view.showSendButton(true);
            }
        }
    }
    //endregion

    //region Message handler
    @Override
    public void saveMessage(@NonNull String code, @NonNull String description) {
        final ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCode(code);
        itemEntity.setDescription(description);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                database.itemDao().insert(itemEntity);
            }
        });
    }

    @Override
    public void sendMessageToConnectedPeer() {
        view.startTransferService(groupPeerInfoList.get(0).groupOwnerAddress.getHostAddress());
    }

    @Override
    public void showMessage(@NonNull String code, @NonNull String description) {
        view.displayMessage(code + " " + description);
    }
    //endregion

    @Override
    public void updatePeerList(@NonNull Collection<WifiP2pDevice> peers) {
        List<PeerModel> peerList = new ArrayList<>();
        for (WifiP2pDevice device : peers) {
            peerList.add(modelMapper.map(device));
        }
        listAdapterPresenter.populateList(peerList);
    }

    @Override
    public void clearInfo() {
        // Need to clear this list as it might cause unwanted behaviors.
        // Like trying to connect to an old IP or similar
        groupPeerInfoList.clear();
        // Clearing the data in the adapter list
        listAdapterPresenter.populateList(Collections.<PeerModel>emptyList());
        // Clearing information about the group formation
        view.displayDeviceInfo("");
        // Hiding send button
        view.showSendButton(false);

//        deletePersistentGroup(manager, channel);
    }

    private static void deletePersistentGroup(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        try {
            Method method = WifiP2pManager.class.getMethod("deletePersistentGroup",
                    WifiP2pManager.Channel.class, int.class, WifiP2pManager.ActionListener.class);

            for (int netId = 0; netId < 32; netId++) {
                method.invoke(manager, channel, netId, null);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
