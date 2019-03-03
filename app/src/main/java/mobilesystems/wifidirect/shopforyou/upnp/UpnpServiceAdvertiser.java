package mobilesystems.wifidirect.shopforyou.upnp;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobilesystems.wifidirect.shopforyou.ErrorMapper;
import mobilesystems.wifidirect.shopforyou.HomeFragmentPresenter;

public class UpnpServiceAdvertiser {

    private static final String TAG = "MOBILE_SYSTEM_ADVERT";

    private static final String UUID = "a240729f-7f3b-4321-9c6c-61d4155489c9";
    private static final String DEVICE_TYPE = "MediaServer:1";
    private static final List<String> SERVICE_TYPE_LIST = new ArrayList<>();

    {
        SERVICE_TYPE_LIST.add("ContentDirectory:1");
    }

    private final @NonNull HomeFragmentPresenter homeFragmentPresenter;
    private final @NonNull WifiP2pManager manager;
    private final @NonNull WifiP2pManager.Channel channel;
    private final @NonNull ErrorMapper errorMapper;

    public UpnpServiceAdvertiser(@NonNull HomeFragmentPresenter homeFragmentPresenter,
                                 @NonNull WifiP2pManager manager,
                                 @NonNull WifiP2pManager.Channel channel,
                                 @NonNull ErrorMapper errorMapper) {
        this.homeFragmentPresenter = homeFragmentPresenter;
        this.manager = manager;
        this.channel = channel;
        this.errorMapper = errorMapper;
    }

    public void startAdvertise() {

        WifiP2pUpnpServiceInfo info = WifiP2pUpnpServiceInfo.newInstance(UUID, DEVICE_TYPE, SERVICE_TYPE_LIST);

        manager.addLocalService(channel, info, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                homeFragmentPresenter.display("Successfully added a service");
            }

            @Override
            public void onFailure(int reasonCode) {
                homeFragmentPresenter.display("Failed to add a service - " + errorMapper.map(reasonCode));
            }
        });
    }

    public void stopAdvertise() {
        manager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                homeFragmentPresenter.display("Successfully cleared a service");
            }

            @Override
            public void onFailure(int reasonCode) {
                homeFragmentPresenter.display("Failed to add a service - " + errorMapper.map(reasonCode));
            }
        });
    }

}
