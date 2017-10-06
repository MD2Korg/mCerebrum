package org.md2k.mcerebrum.UI.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.system.app.ApplicationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.cerebralcortexwebapi.CCWebAPICalls;
import org.md2k.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.system.cerebralcortexwebapi.utils.ApiUtils;
import org.md2k.system.internet.download.DownloadInfo;

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

public class ActivityLogin extends AppCompatActivity {
    BootstrapButton buttonLogin, buttonCancel;
    Subscription subscription;
    MaterialDialog materialDialog;
    CerebralCortexWebApi ccService;
    CCWebAPICalls ccWebAPICalls;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonCancel = (BootstrapButton) findViewById(R.id.button_login_cancel);
        buttonLogin = (BootstrapButton) findViewById(R.id.button_login);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText editTextUserName = (MaterialEditText) findViewById(R.id.edittext_username);
                MaterialEditText editTextPassword = (MaterialEditText) findViewById(R.id.edittext_password);
                MaterialEditText editTextServer = (MaterialEditText) findViewById(R.id.edittext_login_server);
                if (editTextUserName.getText().toString().equals("") ||
                        editTextPassword.getText().toString().equals("") ||
                        editTextServer.getText().toString().equals("")) {
                    Toasty.error(ActivityLogin.this, "Error: Invalid Username and/or password and/or Server name", Toast.LENGTH_SHORT, true).show();
                } else {
                    String userName = editTextUserName.getText().toString();
                    String password = editTextPassword.getText().toString();
                    String server = editTextServer.getText().toString();
                    tryLogin(userName, convertSHA(password), server);
                }
            }
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
        materialDialog = Dialog.progressIndeterminate(this, "Connecting to the server...").show();


/*
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
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        dataManager.resetDataCP();
//                        activityMain.applicationManager=new ApplicationManager(ActivityLogin.this, dataManager.getDataCPManager().getAppCPs());
                        Toasty.success(ActivityLogin.this, "Successfully Logged in",Toast.LENGTH_SHORT).show();
                        saveData(userName, uuid, token, password, serverName);
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
                        Toasty.error(ActivityLogin.this, e.getMessage()).show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
//                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

*/
    }

/*
    void saveData(String userTitle, String uuid, String token, String hashPassword, String server) {
        DataManager dataManager = ((ActivityMain) getActivity()).dataManager;
        dataManager.getDataCPManager().getUserCP().set(MyApplication.getContext(), userTitle, uuid, token, true, hashPassword);
        dataManager.getDataCPManager().getConfigCP().setDownloadLink(MyApplication.getContext(), server);

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
