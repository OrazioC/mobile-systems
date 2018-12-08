package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

public interface HomeFragmentContract {
    interface View {

        void displayConfirmationMessage(@NonNull String confirmationMessage);

        void displayError(@NonNull String errorMessage);

        void displayDeviceInfo(@NonNull String deviceInfo,
                               @NonNull String groupOwnerIpAddress);

        void startTransferService(@NonNull String address);
    }

    interface Presenter {

        void init();

        void requestDeviceConnectionInfo();

        void sendMessageToConnectedPeer();

        void register();

        void discover();

        void unregisterServiceRequest();

        void stopDiscovery();

        void destroyGroup();

        void cancelAnyOngoingGroupNegotiation();

        void saveMessage(@NonNull String code, @NonNull String description);

        void resetData();
    }
}
