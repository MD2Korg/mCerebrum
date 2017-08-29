package org.md2k.mcerebrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.login.ActivityLogin;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivityConfigureStudy extends AppCompatActivity {
    public static final int TYPE_LOGIN = 100;
    public static final int TYPE_GENERAL = 101;
    public static final int TYPE_BARCODE = 102;
    public static final int TYPE_DOWNLOAD = 103;
    Subscription subscription;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configure_study);
        final FancyButton button_general = (FancyButton) findViewById(R.id.button_usage_general);
        button_general.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                downloadConfig();
            }
        });

        final FancyButton button_login = (FancyButton) findViewById(R.id.button_usage_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActivityConfigureStudy.this, ActivityLogin.class);
                startActivityForResult(intent, TYPE_LOGIN);
            }
        });
        final FancyButton button_download = (FancyButton) findViewById(R.id.button_usage_dl);
        button_download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ActivityConfigureStudy.this, "Not implemented yet", Toast.LENGTH_LONG).show();
            }
        });
        final FancyButton button_barcode = (FancyButton) findViewById(R.id.button_usage_scan);
        button_barcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ActivityConfigureStudy.this, "Not implemented yet", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TYPE_LOGIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("type", TYPE_LOGIN);
                setResult(Activity.RESULT_OK, returnIntent);
                returnIntent.putExtra("config_file", data.getStringExtra("config_file"));
                finish();
            }
        }
    }

    void downloadConfig() {

        ConfigManager configManager = new ConfigManager();
        materialDialog = new MaterialDialog.Builder(this)
                .content("Downloading configuration file...")
                .progress(false, 100, true)
                .show();
        subscription = configManager.downloadAndExtract(this, Constants.CONFIG_DEFAULT_FILENAME)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("type", TYPE_GENERAL);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityConfigureStudy.this, "Error: Download failed (e=" + e.getMessage() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        ;
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }
}
