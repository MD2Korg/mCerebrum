package org.md2k.mcerebrum.phonesensor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.commons.permission.ResultCallback;
import org.md2k.mcerebrum.core.access.MCerebrum;

import es.dmoral.toasty.Toasty;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observer;
import rx.Subscription;

import static org.md2k.mcerebrum.phonesensor.PrefsFragmentSettings.REQUEST_CHECK_SETTINGS;


public class ActivityPermission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 101);
            }
        }
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (!result) {
                    MCerebrum.setPermission(ActivityPermission.this, false);
                    Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                } else {
                    checkAllPermission();
                }
            }
        });
    }

    public void checkAllPermission() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getBaseContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(ActivityPermission.this, result,
                        9000, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Toasty.error(getApplicationContext(), "Google play service is disabled/not updated", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                finish();
                            }
                        }).show();
            } else {
                Toasty.error(getApplicationContext(), "Google play service is DISABLED/NOT UPDATED", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        } else {
            enableGPS();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9000) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int result = googleAPI.isGooglePlayServicesAvailable(this);
            if (result != ConnectionResult.SUCCESS) {
                Toasty.error(getApplicationContext(), "Google play service is disabled/not updated", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            } else {
                enableGPS();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // All required changes were successfully made
                MCerebrum.setPermission(this, true);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(this, false);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        }
    }

    private Subscription updatableLocationSubscription;

    public void enableGPS() {
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);

        updatableLocationSubscription = new ReactiveLocationProvider(this)
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .setAlwaysShow(true)  //Refrence: http://stackoverflow.com/questions/29824408/google-play-services-locationservices-api-new-option-never
                                .build()
                ).subscribe(new Observer<LocationSettingsResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LocationSettingsResult locationSettingsResult) {
                        try {
                            Status status = locationSettingsResult.getStatus();
                            if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                status.startResolutionForResult(ActivityPermission.this, REQUEST_CHECK_SETTINGS);
                            } else {
                                MCerebrum.setPermission(ActivityPermission.this, true);
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        } catch (Exception e) {
                            MCerebrum.setPermission(ActivityPermission.this, false);
                            Toast.makeText(getBaseContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            finish();
                        }

                    }
                });
    }

    @Override
    public void onDestroy() {
        if (updatableLocationSubscription != null && !updatableLocationSubscription.isUnsubscribed())
            updatableLocationSubscription.unsubscribe();
        super.onDestroy();
    }

}
