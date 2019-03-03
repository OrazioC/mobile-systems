package mobilesystems.wifidirect.shopforyou.upnp;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import mobilesystems.wifidirect.shopforyou.ErrorMapper;
import mobilesystems.wifidirect.shopforyou.HomeFragmentPresenter;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapterContract;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModel;
import mobilesystems.wifidirect.shopforyou.peerlist.PeerModelMapper;

public class UpnpServiceDiscovery {

    private static final String TAG = "MOBILE_SYSTEM_DISCOVERY";

    private final @NonNull HomeFragmentPresenter homeFragmentPresenter;
    private final @NonNull WifiP2pManager manager;
    private final @NonNull WifiP2pManager.Channel channel;
    private final @NonNull ErrorMapper errorMapper;
    private final @NonNull PeerListAdapterContract.Presenter listAdapterPresenter;
    private final @NonNull PeerModelMapper modelMapper;

    private @Nullable WifiP2pUpnpServiceRequest serviceRequest;


    public UpnpServiceDiscovery(@NonNull  HomeFragmentPresenter homeFragmentPresenter,
                                @NonNull WifiP2pManager manager,
                                @NonNull WifiP2pManager.Channel channel,
                                @NonNull ErrorMapper errorMapper,
                                @NonNull PeerListAdapterContract.Presenter listAdapterPresenter,
                                @NonNull PeerModelMapper modelMapper) {
        this.homeFragmentPresenter = homeFragmentPresenter;
        this.manager = manager;
        this.channel = channel;
        this.errorMapper = errorMapper;
        this.listAdapterPresenter = listAdapterPresenter;
        this.modelMapper = modelMapper;
    }

    public void startDiscovery() {
        attachUpnpListeners();
        createServiceRequest();
        discoverService();
    }

    private void attachUpnpListeners() {
        WifiP2pManager.UpnpServiceResponseListener upnpServiceListener = new WifiP2pManager.UpnpServiceResponseListener() {
            @Override
            public void onUpnpServiceAvailable(List<String> uniqueServiceNames, WifiP2pDevice device) {
                Log.d(TAG, "Found Service in device, :" + device.deviceName +
                        ", with " + uniqueServiceNames.size() + " services");
                List<PeerModel> peerList = Collections.singletonList(modelMapper.map(device));
                listAdapterPresenter.populateList(peerList);
            }
        };

        manager.setUpnpServiceResponseListener(channel, upnpServiceListener);
    }

    private void createServiceRequest() {
        serviceRequest = WifiP2pUpnpServiceRequest.newInstance();

        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        homeFragmentPresenter.display("Added service discovery request");
                    }

                    @Override
                    public void onFailure(@IntRange(from = 0, to = 3) int reasonCode) {
                        homeFragmentPresenter.display("Failed adding service discovery request - "
                                + errorMapper.map(reasonCode));
                    }
                });
    }

    private void discoverService() {
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                homeFragmentPresenter.display("Service discovery initiated");
            }

            @Override
            public void onFailure(@IntRange(from = 0, to = 3) int reasonCode) {
                homeFragmentPresenter.display( "Service discovery failed - " + errorMapper.map(reasonCode));
            }
        });
    }

    public void unregisterServiceRequest() {
        if (serviceRequest != null) {
            manager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    homeFragmentPresenter.display( "Removed service discovery request");
                }

                @Override
                public void onFailure(@IntRange(from = 0, to = 2) int reasonCode) {
                    homeFragmentPresenter.display("Failed removing service discovery request - "
                            + errorMapper.map(reasonCode));
                }
            });
        }
    }
}
