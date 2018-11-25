package mobilesystems.wifidirect.shopforyou;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import mobilesystems.wifidirect.shopforyou.peerlist.PeerListAdapter;
import mobilesystems.wifidirect.shopforyou.broadcastreceiver.WiFi2P2BroadcastReceiver;

import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment implements HomeFragmentContract.View {


    private TextView deviceInfoTextView;
    private TextView ownerIPAddressTextView;
    private TextView messageFromTheOtherSideTextView;

    private HomeFragmentContract.Presenter presenter;
    private WiFi2P2BroadcastReceiver broadcastReceiver;
    private final IntentFilter intentFilter = new IntentFilter();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public @Nullable
    View onCreateView(@NonNull LayoutInflater inflater,
                      @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        RecyclerView peerList = rootView.findViewById(R.id.peer_list);
        PeerListAdapter adapter = new PeerListAdapter();
        peerList.setAdapter(adapter);
        peerList.setLayoutManager(new LinearLayoutManager(getContext()));

        WifiP2pManager manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        // Needs android.permission.ACCESS_WIFI_STATE, android.permission.CHANGE_WIFI_STATE
        WifiP2pManager.Channel channel = manager.initialize(getContext(), getMainLooper(), null);
        presenter = new HomeFragmentPresenter(this, adapter, manager, channel);
        presenter.init();
        broadcastReceiver = new WiFi2P2BroadcastReceiver(presenter);

        deviceInfoTextView = rootView.findViewById(R.id.device_info);
        ownerIPAddressTextView = rootView.findViewById(R.id.group_owner_ip_address);
        messageFromTheOtherSideTextView = rootView.findViewById(R.id.message_from_other_peer);

        View registerServiceTextView = rootView.findViewById(R.id.register_service);
        View discoverServiceTextView = rootView.findViewById(R.id.discover_service);

        registerServiceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { presenter.register();
            }
        });
        discoverServiceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { presenter.discover();
            }
        });

        View sendMessageCta = rootView.findViewById(R.id.send_message);
        sendMessageCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendMessageToConnectedPeer();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //To prevent memory leaks
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unregisterServiceRequest();
        presenter.stopDiscovery();
        presenter.destroyGroup();
    }

    @Override
    public void displayWiFiStatus(@NonNull String status) {
//        Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayConfirmationMessage(@NonNull String confirmationMessage) {
        Toast.makeText(getContext(), confirmationMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayError(@NonNull String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayDeviceInfo(@NonNull String deviceInfo,
                                  @NonNull String groupOwnerIpAddress) {
        deviceInfoTextView.setText(deviceInfo);
        ownerIPAddressTextView.setText(groupOwnerIpAddress);
    }

    @Override
    public void displayMessageFromOtherPeer(@NonNull String message) {
        messageFromTheOtherSideTextView.setText(message);
    }

    @Override
    public void startTransferService(@NonNull String address) {
        getActivity().startService(InfoTransferService.createIntent(getContext(), address));
    }
}
