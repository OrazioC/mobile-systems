package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

public interface HomeFragmentContract {
    interface View {

        void displayWiFiStatus(@NonNull String status);

        void displayConfirmationMessage(@NonNull String confirmationMessage);

        void displayError(@NonNull String errorMessage);
    }

    interface Presenter {

        void init();

        void showWiFiStatus(boolean isWiFiEnabled);

        void startDiscovery();

        void populatePeerList();

        void requestDeviceConnectionInfo();
    }
}
