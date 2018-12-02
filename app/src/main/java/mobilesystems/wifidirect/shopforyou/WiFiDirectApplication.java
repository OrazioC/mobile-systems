package mobilesystems.wifidirect.shopforyou;

import android.app.Application;

import mobilesystems.wifidirect.shopforyou.database.AppDatabase;

public class WiFiDirectApplication extends Application {

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }
}
