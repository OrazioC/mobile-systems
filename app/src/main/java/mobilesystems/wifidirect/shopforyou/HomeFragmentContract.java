package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

public interface HomeFragmentContract {
    interface View {

        void displayWiFiStatus(@NonNull String status);

        void displayConfirmationMessage(@NonNull String confirmationMessage);

        void displayError(@NonNull String errorMessage);
    }

    interface Presenter {

        void startDiscovery();

        void populateList();

        void showWiFiStatus(boolean isWiFiEnabled);
    }
}
