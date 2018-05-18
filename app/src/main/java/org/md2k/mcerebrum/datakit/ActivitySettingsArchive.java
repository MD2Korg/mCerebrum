package org.md2k.mcerebrum.datakit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.md2k.mcerebrum.core.datakitapi.messagehandler.ResultCallback;
import org.md2k.mcerebrum.R;

public class ActivitySettingsArchive extends AppCompatActivity {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = ActivitySettingsArchive.class.getSimpleName();

    /**
     * Upon creation, this activity creates a new <code>PermissionInfo</code> object to fetch permissions.
     *
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_archive);
        getFragmentManager().beginTransaction().replace(R.id.layout_preference_fragment,
                new PrefsFragmentSettingsArchive()).commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    /**
     * Finishes the activity if the home button is pressed on the device.
     *
     * @param item Menu item that was selected.
     * @return Whether home or back was pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
