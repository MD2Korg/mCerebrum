package org.md2k.mcerebrum.app;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.core.access.MCerebrumStatus;
import org.md2k.mcerebrum.data.MySharedPreference;

public class MCerebrumController {
    private String packageName;
    private MCerebrumStatus mCerebrumStatus;
    private boolean mCerebrumSupported;
    private ServiceCommunication serviceCommunication;
    private boolean initialized;
    private boolean started;

    MCerebrumController(MCerebrumStatus mCerebrumStatus, CApp cApp) {
        this.packageName = cApp.getPackage_name();
        this.mCerebrumStatus = mCerebrumStatus;
        this.mCerebrumSupported = false;
        this.started = false;
        this.initialized = new MySharedPreference().getBoolean(packageName + "_INITIALIZED", false);
    }

    void startService() {
        try {
            if (started) return;
            serviceCommunication = new ServiceCommunication();
            serviceCommunication.start(MyApplication.getContext(), packageName, new ResponseCallback() {
                @Override
                public void onResponse(boolean isConnected) {
                    mCerebrumSupported = isConnected;
                    started = isConnected;
                    if (isConnected) {
                        started = true;
                        setStatus();
                    }
                }
            });

        } catch (Exception e) {
            serviceCommunication = null;
            mCerebrumSupported = false;
            started = false;
        }
    }

    void stopService(Bundle bundle) {
        try {
            serviceCommunication.exit(bundle);
            started = false;

        } catch (Exception e) {
            serviceCommunication = null;
            started = false;
        }
    }

    public void report(Bundle bundle) {
        try {
            serviceCommunication.report(bundle);
        } catch (Exception ignored) {
        }
    }

    public void configure(Bundle bundle) {
        try {
            serviceCommunication.configure(bundle);
        } catch (Exception ignored) {
        }

    }

    public void clear(Bundle bundle) {

        try {
            serviceCommunication.clear(bundle);
        } catch (Exception ignored) {
        }
    }

    public void startBackground(Bundle bundle) {
        try {
            serviceCommunication.startBackground(bundle);
        } catch (Exception ignored) {
        }
    }

    public void stopBackground(Bundle bundle) {
        try {
            serviceCommunication.stopBackground(bundle);
        } catch (Exception ignored) {
        }
    }

    public void initialize(Bundle bundle) {
        try {
            serviceCommunication.initialize(bundle);
        } catch (Exception ignored) {
        }
    }

    void setStatus() {
        try {
            MCerebrumStatus m = serviceCommunication.getmCerebrumStatus();
            if (m != null) {
                this.mCerebrumStatus = m;
                if (!initialized) {
                    setInitialized(true);
                    initialize(null);
                }
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("connection"));
            }
        } catch (Exception ignored) {
        }
    }

    public boolean ismCerebrumSupported() {
        return mCerebrumSupported;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isConfigurable() {
        return mCerebrumStatus.isConfigurable();
    }

    public boolean isConfigured() {
        return mCerebrumStatus.isConfigured();
    }

    public boolean hasClear() {
        return mCerebrumStatus.hasClear();
    }

    public boolean isEqualDefault() {
        return mCerebrumStatus.hasClear();
    }

    public void launch(Bundle bundle) {
        try {
            serviceCommunication.launch(bundle);
            return;
        } catch (Exception ignored) {
            Log.d("abc","e="+ignored.getMessage());
        }
        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        MyApplication.getContext().startActivity(intent);
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
        new MySharedPreference().set(packageName + "_INITIALIZED", initialized);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
