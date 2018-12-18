package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import mobilesystems.wifidirect.shopforyou.database.AppDatabase;
import mobilesystems.wifidirect.shopforyou.database.ItemEntity;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerConnectionStatusMapper;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterContract;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterPresenter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListener;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModel;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModelMapper;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter, WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "MOBILE_SYSTEM";
    private static final String SERVICE_INSTANCE = "_mobilesystem";
    private static final String TRANSPORT_PROTOCOL = "_tcp";
    private static final String SERVICE_TYPE = "_presence." + TRANSPORT_PROTOCOL;

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private @NonNull WifiP2pManager manager;
    private @NonNull WifiP2pManager.Channel channel;
    private @NonNull PeerModelMapper modelMapper;
    private @NonNull ErrorMapper errorMapper;
    private @NonNull AppDatabase database;

    private @NonNull List<WifiP2pInfo> groupPeerInfoList = new ArrayList<>();
    private WifiP2pDnsSdServiceRequest serviceRequest;


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

    //region Register Service
    @Override
    public void register() {
        Map<String, String> record = Collections.emptyMap();
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Service registered");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                view.displayError("Failed to add a service - "
                        + errorMapper.map(reasonCode));
            }
        });
    }
    //endregion

    //region Discover Service
    @Override
    public void discover() {
        attachDnsSdListeners();
        createServiceRequest();
        discoverService();
    }

    private void attachDnsSdListeners() {
        WifiP2pManager.DnsSdServiceResponseListener serviceListener =
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType,
                                                        WifiP2pDevice device) {

                        if (SERVICE_INSTANCE.equalsIgnoreCase(instanceName)) {
                            List<PeerModel> peerList = Collections.singletonList(modelMapper.map(device));
                            listAdapterPresenter.populateList(peerList);
                        } else {
                            Log.d(TAG, "received service: " + instanceName);
                        }
                    }
                };
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener =
                new WifiP2pManager.DnsSdTxtRecordListener() {
                    /**
                     * A new TXT record is available.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG, "Txt record available: " + device.deviceName);
                    }
                };
        manager.setDnsSdResponseListeners(channel, serviceListener, txtRecordListener);
    }

    private void createServiceRequest() {
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        view.displayConfirmationMessage("Added service discovery request");
                    }

                    @Override
                    public void onFailure(@IntRange(from = 0, to = 3) int reasonCode) {
                        view.displayError("Failed adding service discovery request - "
                                + errorMapper.map(reasonCode));
                    }
                });
    }

    private void discoverService() {
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Service discovery initiated");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 3) int reasonCode) {
                view.displayError("Service discovery failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }
    //endregion

    //region Release Resources
    @Override
    public void unregisterServiceRequest() {
        if (serviceRequest != null) {
            manager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    view.displayConfirmationMessage("Removed service discovery request");
                }

                @Override
                public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                    view.displayError("Failed removing service discovery request - "
                            + errorMapper.map(reasonCode));
                }
            });
        }
    }

    @Override
    public void stopDiscovery() {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Service discovery stopped");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                view.displayError("Service discovery stop failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }

    @Override
    public void destroyGroup() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Group removed");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                view.displayError("Group removal failed - "
                        + errorMapper.map(reasonCode));
            }
        });
    }
    //endregion

    //region Connection handler
    private void connect(@NonNull String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;

        unregisterServiceRequest();
        /*
         * registers for WIFI_P2P_CONNECTION_CHANGED_ACTION
         */
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Connection requested");
            }

            @Override
            public void onFailure(int reasonCode) {
                view.displayError("Group removal failed" + errorMapper.map(reasonCode));
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
        this.groupPeerInfoList.add(info);

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
        view.displayConfirmationMessage(code + " " + description);
    }
    //endregion

    @Override
    public void updatePeerList(Collection<WifiP2pDevice> peers) {
        List<PeerModel> peerList = new ArrayList<>();
        for (WifiP2pDevice device : peers) {
            peerList.add(modelMapper.map(device));
        }
        listAdapterPresenter.populateList(peerList);
    }
}
