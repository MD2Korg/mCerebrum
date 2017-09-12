package org.md2k.mcerebrum.UI.joinstudy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.user.UserInfo;
import org.md2k.mcerebrum.internet.download.DownloadInfo;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FragmentJoinStudy extends Fragment {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private View mContentView;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    Subscription subscription;
    MaterialDialog materialDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_join_study, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        final BootstrapButton button_cancel = (BootstrapButton) view.findViewById(R.id.button_login_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                ((ActivityMain)getActivity()).updateUI();
            }
        });
        final BootstrapButton button_login = (BootstrapButton) view.findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText materialEditText = (MaterialEditText) view.findViewById(R.id.edittext_username);
                MaterialEditText materialEditText1 = (MaterialEditText) view.findViewById(R.id.edittext_password);
                MaterialEditText materialEditText2 = (MaterialEditText) view.findViewById(R.id.edittext_login_server);
                if(materialEditText.getText().toString().equals("") ||
                        materialEditText1.getText().toString().equals("") ||
                        materialEditText2.getText().toString().equals("") ||
                        !materialEditText.getText().toString().equals("abc") ||
                        !materialEditText1.getText().toString().equals("123")){
                    Toasty.error(getContext(), "Error: Invalid Username and/or password", Toast.LENGTH_SHORT, true).show();
                }else{
                    Toasty.success(getActivity(), "Successfully Logged in", Toast.LENGTH_SHORT, true).show();
                    downloadConfig(Constants.CONFIG_MPERF_FILENAME, materialEditText.getText().toString());
                }
            };


        });

    }
    public void downloadConfig(String downloadURL, final String userName) {
        final ActivityMain activityMain=(ActivityMain)getActivity();
        ConfigManager configManager=activityMain.configManager;
        final UserInfo userInfo=activityMain.userInfo;
        materialDialog = Dialog.progress(getActivity(), "Downloading configuration file...").show();
        subscription = configManager.downloadAndExtract(getContext(), downloadURL)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        if(!activityMain.readConfig()){
                            Toasty.error(getContext(), "Error: Configuration file format error").show();
                            activityMain.prepareConfig();
                        }else {
                            if(userName!=null) {
                                userInfo.setLoggedIn(true);
                                userInfo.setTitle(userName);
                                activityMain.updateUI();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getContext(), "Error: Download failed (e=" + e.getMessage() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }

    @Override
    public void onDestroy(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }
}
