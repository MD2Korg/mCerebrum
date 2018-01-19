package org.md2k.mcerebrum.login;

import android.net.sip.SipAudioCall;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.core.access.configinfo.ConfigCP;
import org.md2k.mcerebrum.core.access.serverinfo.ServerCP;
import org.md2k.mcerebrum.core.access.userinfo.UserCP;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.ServerManager;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectStats;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class LoginModelImpl implements LoginModel {

    AuthResponse authResponse;
    @Override
    public void login(final String username, final String password, final String servername, final OnLoginFinishedListener listener) {


        // Mock login. I'm creating a handler to delay the answer a couple of seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(username)) {
                    listener.onUsernameError();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    listener.onPasswordError();
                    return;
                }
                authResponse = ServerManager.authenticate(servername, username, password);
                if (authResponse == null){
                    listener.onUsernameError();
                }
             //   return ServerManager.getConfigFiles(servername, authResponse.getAccessToken());

                listener.onSuccess();
            }
        }, 2000);
    }


}
