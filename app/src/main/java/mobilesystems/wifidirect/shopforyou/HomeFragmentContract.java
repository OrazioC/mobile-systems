package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;

public interface HomeFragmentContract {
    interface View {

        void displayWiFiStatus(@NonNull String status);

        void displayDiscoveryInitiated(@NonNull String confirmationMessage);

        void displayDiscoveryFailure(@NonNull String errorMessage);
    }
    interface Presenter {

        void startDiscovery();

        void populateList();

        void showWiFiStatus(boolean isWiFiEnabled);
    }
}
