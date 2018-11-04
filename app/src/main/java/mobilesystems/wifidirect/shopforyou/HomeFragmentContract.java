package mobilesystems.wifidirect.shopforyou;

public interface HomeFragmentContract {
    interface View {

        void displayWiFiStatus(String status);
    }
    interface Presenter {

        void populateList();

        void showWiFiStatus(boolean isWiFiEnabled);
    }
}
