package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import mobilesystems.wifidirect.shopforyou.database.AppDatabase;
import mobilesystems.wifidirect.shopforyou.database.ItemEntity;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterContract;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterPresenter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListener;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModel;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter, WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "MOBILE_SYSTEM";
    private static final String SERVICE_INSTANCE = "_mobilesystem";
    private static final String TRANSPORT_PROTOCOL = "_tcp";
    private static final String SERVICE_TYPE = "_presence." + TRANSPORT_PROTOCOL;

    private @NonNull HomeFragmentContract.View view;
    private @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private @NonNull WifiP2pManager manager;
    private @NonNull WifiP2pManager.Channel channel;
    private @NonNull DeviceConnectionStatusMapper deviceConnectionStatusMapper;
    private @NonNull DiscoveryFailureErrorMapper errorMapper;
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
        this.deviceConnectionStatusMapper = new DeviceConnectionStatusMapper();
        this.errorMapper = new DiscoveryFailureErrorMapper();
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

    //region WifiP2PManager
    @Override
    public void requestDeviceConnectionInfo() {
        // WifiP2pManager.ConnectionInfoListener
        manager.requestConnectionInfo(channel, this);
    }

    @Override
    public void sendMessageToConnectedPeer() {
        view.startTransferService(groupPeerInfoList.get(0).groupOwnerAddress.getHostAddress());
    }

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
            public void onFailure(int error) {
                view.displayError("Failed to add a service");
            }
        });
    }

    @Override
    public void discover() {
        attachDnsSdListeners();
        createServiceRequest();
        discoverService();
    }

    //region Discover Service
    private void attachDnsSdListeners() {
        WifiP2pManager.DnsSdServiceResponseListener serviceListener =
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType,
                                                        WifiP2pDevice device) {

                        if (SERVICE_INSTANCE.equalsIgnoreCase(instanceName)) {
                            Log.d(TAG, "received service: " + SERVICE_INSTANCE);
                            List<PeerModel> peerList = Collections.singletonList(
                                    new PeerModel(device.deviceName, device.deviceAddress,
                                            device.primaryDeviceType,
                                            deviceConnectionStatusMapper.map(device.status)));
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
                    public void onFailure(int arg0) {
                        view.displayError("Failed adding service discovery request");
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
            public void onFailure(int arg0) {
                view.displayError("Service discovery failed");
            }
        });
    }
    //endregion

    @Override
    public void unregisterServiceRequest() {
        if (serviceRequest != null) {
            manager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    view.displayConfirmationMessage("Removed service discovery request");
                }

                @Override
                public void onFailure(int reason) {
                    view.displayError("Failed removing service discovery request");
                }
            });
        }
    }

    //region Release Resources
    @Override
    public void stopDiscovery() {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Service discovery stopped");
            }

            @Override
            public void onFailure(int arg0) {
                view.displayError("Service discovery stop failed");
            }
        });
    }

    @Override
    public void destroyGroup() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                view.displayError("Group removal failed");
            }

            @Override
            public void onSuccess() {
                view.displayConfirmationMessage("Group removed");
            }

        });
    }
    //endregion

    private void connect(@NonNull String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;

//        unregisterServiceRequest();
        Log.d(TAG, "connection requested");
        /*
         * registers for WIFI_P2P_CONNECTION_CHANGED_ACTION
         */
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connection successful");
            }

            @Override
            public void onFailure(int reason) {
                view.displayError(errorMapper.map(reason));
            }
        });
    }
    //endregion

    //endregion

    //region WifiP2pManager.ConnectionInfoListener
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
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

    @Override
    public void cancelAnyOngoingGroupNegotiation() {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Cancelled any ongoing group negotiation");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Cancel ongoing group negotiation failed" + reason);
            }
        });
    }

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
    public void resetData() {
        // Need to clear this list as it might cause unwanted behaviors.
        // Like trying to connect to an old IP or similar
        this.groupPeerInfoList.clear();
        // Clearing the data in the adapter list
        this.listAdapterPresenter.populateList(Collections.<PeerModel>emptyList());
        // clearing information about the group formation
        view.displayDeviceInfo("", "");
    }
}
