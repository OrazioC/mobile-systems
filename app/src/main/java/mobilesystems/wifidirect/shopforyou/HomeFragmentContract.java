package mobilesystems.wifidirect.shopforyou;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;

import java.util.Collection;

public interface HomeFragmentContract {
    interface View {

        void displayMessage(@NonNull String confirmationMessage);

        void displayDeviceInfo(@NonNull String deviceInfo);

        void startTransferService(@NonNull String address);

        void showSendButton(boolean show);
    }

    interface Presenter {

        void init();

        void requestDeviceConnectionInfo();

        void sendMessageToConnectedPeer();

        void display(@NonNull String message);

        void register();

        void discover();

        void unregisterServiceRequest();

        void stopDiscovery();

        void cancelAnyOngoingGroupNegotiation();

        void destroyGroup();

        void saveMessage(@NonNull String code, @NonNull String description);

        void showMessage(@NonNull String code, @NonNull String description);

        void updatePeerList(@NonNull Collection<WifiP2pDevice> clientList);

        void clearInfo();
    }
}
