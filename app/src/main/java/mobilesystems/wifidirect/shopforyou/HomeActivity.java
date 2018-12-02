package mobilesystems.wifidirect.shopforyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.stetho.Stetho;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        /*
         * to check DB -> chrome://inspect/#devices
         */
        Stetho.initializeWithDefaults(this);
    }
}
