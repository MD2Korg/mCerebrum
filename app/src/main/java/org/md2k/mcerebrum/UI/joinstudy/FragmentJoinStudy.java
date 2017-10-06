package org.md2k.mcerebrum.UI.joinstudy;

import android.os.Bundle;
import android.os.Environment;
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
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.app.ApplicationManager;
import org.md2k.system.cerebralcortexwebapi.ServerManager;
import org.md2k.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.system.constant.MCEREBRUM;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

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
    DataManager dataManager;
    ActivityMain activityMain;
    AuthResponse authResponse;
    MinioObjectStats minioObject;
    String userName, password, serverName;


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
                    userName = editTextUserName.getText().toString();
                    password = convertSHA(editTextPassword.getText().toString());
                    serverName = editTextServer.getText().toString();
                    tryLogin();
                }
            }

            ;


        });
    }

    private String convertSHA(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
        return "";
    }

    void tryLogin() {
        materialDialog = Dialog.progressIndeterminate(getActivity(), "Connecting to the server...").show();
        subscription = Observable.just(true).subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<Boolean, Observable<List<MinioObjectStats>>>() {
                    @Override
                    public Observable<List<MinioObjectStats>> call(Boolean aBoolean) {
                        authResponse = ServerManager.authenticate(serverName, userName, password);
                        if (authResponse == null)
                            return Observable.error(new Throwable("Invalid Username/password/server name"));
                        return Observable.just(ServerManager.getConfigFiles(serverName, authResponse.getAccessToken()));
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<MinioObjectStats>, Observable<MinioObjectStats>>() {
                    @Override
                    public Observable<MinioObjectStats> call(List<MinioObjectStats> minioObjectStatses) {
                        return getObservableFile(minioObjectStatses);

                    }
                }).observeOn(Schedulers.newThread())
                .flatMap(new Func1<MinioObjectStats, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(MinioObjectStats minioObjectStats) {
                        minioObject=minioObjectStats;
                        if(!ServerManager.download(serverName, authResponse.getAccessToken(), minioObjectStats.getObjectName()))
                        return Observable.error(new Throwable("Download failed"));
                        return Observable.just(true);
                    }
                }).subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        String a = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        if (!unzipFile(a+"/config.zip", Constants.CONFIG_ROOT_DIR()))
                            return Observable.error(new Throwable("Failed to unzip"));
                        else {
                            if(!dataManager.getDataFileManager().read()){
                                dataManager.loadDefault();
                                return Observable.error(new Throwable("Configuration file format error"));
                            }else
                                return Observable.just(true);
                        }
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        Toasty.success(getActivity(), "Configuration file downloaded", Toast.LENGTH_SHORT).show();
                        dataManager.resetDataCP(MCEREBRUM.CONFIG.TYPE_SERVER, userName);
                        dataManager.getDataCPManager().getServerCP().set(MyApplication.getContext(), serverName, userName, authResponse.getUserUuid(), password, authResponse.getAccessToken(), minioObject.getObjectName(), minioObject.getLastModified(), minioObject.getLastModified());
                        activityMain.updateUI();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getContext(), e.getMessage()).show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(Boolean downloadInfo) {
//                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

    }

    Observable<MinioObjectStats> getObservableFile(final List<MinioObjectStats> list) {
        Toasty.success(getActivity(), "Successfully Logged in", Toast.LENGTH_SHORT).show();
        materialDialog.dismiss();
        if (list.size() == 0)
            return Observable.error(new Throwable("Configuration file doesn't exist"));
        if (list.size() == 1) return Observable.just(list.get(0));
        return Observable.create(new Observable.OnSubscribe<MinioObjectStats>() {
            @Override
            public void call(final Subscriber<? super MinioObjectStats> subscriber) {
                try {
                    String[] fileName = new String[list.size()];
                    for (int i = 0; i < list.size(); i++)
                        fileName[i] = list.get(i).getObjectName();
                    Dialog.singleChoice(getActivity(), "Select Configuration File", fileName, -1, new DialogCallback() {
                        @Override
                        public void onSelected(String value) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getObjectName().equals(value)) {
                                    subscriber.onNext(list.get(i));
                                    subscriber.onCompleted();
                                    materialDialog = Dialog.progressIndeterminate(getActivity(), "Downloading configuration file...").show();

                                }
                            }
                            subscriber.onError(new Throwable("File is not selected"));
                        }
                    }).show();
                } catch (Exception e) {
                    subscriber.onError(e);        // Signal about the error to subscriber
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }
}
