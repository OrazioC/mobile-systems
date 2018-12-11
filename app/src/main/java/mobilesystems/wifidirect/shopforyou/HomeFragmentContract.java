package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

public interface HomeFragmentContract {
    interface View {

        void displayWiFiStatus(@NonNull String status);

        void displayConfirmationMessage(@NonNull String confirmationMessage);

        void displayError(@NonNull String errorMessage);

        void displayDeviceInfo(@NonNull String deviceInfo,
                               @NonNull String groupOwnerIpAddress);

        void displayMessageFromOtherPeer(@NonNull String message);

        void startTransferService(@NonNull String address);
    }

    interface Presenter {

        void init();

        void startDiscovery();

        void populatePeerList();

        void requestDeviceConnectionInfo();

        void displayMessage(@NonNull String message);

        void sendMessageToConnectedPeer();

        void stopDiscovery();

        void destroyGroup();

        void resetData();

        void cancelAnyOngoingGroupNegotiation();
    }
}
