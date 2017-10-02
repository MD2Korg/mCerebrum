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
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.internet.download.DownloadInfo;
import org.md2k.system.cerebralcortexwebapi.CCWebAPICalls;
import org.md2k.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.system.cerebralcortexwebapi.utils.ApiUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    CerebralCortexWebApi ccService;
    CCWebAPICalls ccWebAPICalls;
    DataManager dataManager;
    ActivityMain activityMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_join_study, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        activityMain = (ActivityMain) getActivity();
        dataManager = activityMain.dataManager;
        final BootstrapButton button_cancel = (BootstrapButton) view.findViewById(R.id.button_login_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                ((ActivityMain) getActivity()).updateUI();
            }
        });
        final BootstrapButton button_login = (BootstrapButton) view.findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText editTextUserName = (MaterialEditText) view.findViewById(R.id.edittext_username);
                MaterialEditText editTextPassword = (MaterialEditText) view.findViewById(R.id.edittext_password);
                MaterialEditText editTextServer = (MaterialEditText) view.findViewById(R.id.edittext_login_server);
                if (editTextUserName.getText().toString().equals("") ||
                        editTextPassword.getText().toString().equals("") ||
                        editTextServer.getText().toString().equals("")) {
                    Toasty.error(getContext(), "Error: Invalid Username and/or password and/or Server name", Toast.LENGTH_SHORT, true).show();
                } else {
                    String userName = editTextUserName.getText().toString();
                    String password = editTextPassword.getText().toString();
                    String server = editTextServer.getText().toString();
                    tryLogin(userName, convertSHA(password), server);
                }
            }

            ;


        });
    }
    String convertSHA(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    String uuid, token;
    void tryLogin(final String userName, final String password, final String serverName) {
        materialDialog = Dialog.progressIndeterminate(getActivity(), "Connecting to the server...").show();


        ccService = ApiUtils.getCCService(serverName);
        ccWebAPICalls = new CCWebAPICalls(ccService);
        subscription = Observable.just(true).subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<Boolean, Observable<AuthResponse>>() {
                    @Override
                    public Observable<AuthResponse> call(Boolean aBoolean) {
                        AuthResponse authResponse = ccWebAPICalls.authenticateUser(userName, password);
                        if(authResponse==null) return Observable.error(new Throwable("Invalid Username/password/server name"));
                        uuid=authResponse.getUserUuid();
                        token=authResponse.getAccessToken();

//                        saveData(userName, authResponse.getUserUuid(), authResponse.getAccessToken(), password);
                        return Observable.just(authResponse);
                    }
                }).flatMap(new Func1<AuthResponse, Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(AuthResponse ar) {
                        return dataManager.getDataFileManager().downloadAndExtractFromServer(serverName, userName, password);
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        dataManager.resetDataCP();
                        activityMain.applicationManager=new ApplicationManager(getActivity(), dataManager.getDataCPManager().getAppCPs());
                        Toasty.success(getActivity(), "Successfully Logged in",Toast.LENGTH_SHORT).show();
                        saveData(userName, uuid, token, password, serverName);

                        activityMain.updateUI();
                        /*
                        if (!activityMain.readConfig()) {
                            Toasty.error(getContext(), "Error: Configuration file format error").show();
                            activityMain.prepareConfig();
                        } else {
                            activityMain.updateUI();
                        }
*/
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getContext(), e.getMessage()).show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
//                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

    }

    void saveData(String userTitle, String uuid, String token, String hashPassword, String server) {
        DataManager dataManager = ((ActivityMain) getActivity()).dataManager;
        dataManager.getDataCPManager().getUserCP().set(MyApplication.getContext(), userTitle, uuid, token, true, hashPassword);
        dataManager.getDataCPManager().getConfigCP().setDownloadLink(MyApplication.getContext(), server);

    }

/*
    public void downloadConfig(String downloadURL) {

        final ActivityMain activityMain = (ActivityMain) getActivity();
        final DataManager dataManager = activityMain.dataManager;
        materialDialog = Dialog.progressWithBar(getActivity(), "Downloading configuration file...").show();
        subscription = dataManager.getDataFileManager().downloadAndExtract(getContext(), downloadURL)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        dataManager.resetDataCP();
                        activityMain.updateUI();
                        */
/*
                        if (!activityMain.readConfig()) {
                            Toasty.error(getContext(), "Error: Configuration file format error").show();
                            activityMain.prepareConfig();
                        } else {
                            activityMain.updateUI();
                        }
*//*

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
*/

    @Override
    public void onDestroy() {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }
}
