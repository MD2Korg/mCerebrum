package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.commons.permission.ResultCallback;
import org.md2k.mcerebrum.menu.AbstractMenu;

public class ActivityMain extends AbstractActivityMenu {
    private static final int REQUEST_CODE = 100;
    boolean isFirstTime;
    boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirstTime = true;
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (!result) {
                    Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    isPermissionGranted = true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPermissionGranted) return;
        updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
        isFirstTime = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED)
                finish();
            else {
                updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

