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

import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.core.access.Info;

public class AppMC {
    private static final int CONFIGURE = 3;
    private static final int REPORT = 4;
    private static final int START_BACKGROUND = 5;
    private static final int STOP_BACKGROUND = 6;
    private static final int INFO = 2;
    private static final int INIT =7 ;
    private static final int CLEAR = 8;
    private AppInfo appInfo;
    private ServiceCommunication serviceCommunication;

    AppMC(AppInfo appInfo) {
        this.appInfo = appInfo;
        appInfo.setmCerebrumSupported(false);
        appInfo.setmCerebrum(null);
        if(appInfo.isInstalled())
            startService();
    }

    void startService() {
        try {
            serviceCommunication = new ServiceCommunication();
            serviceCommunication.start(MyApplication.getContext(), appInfo.getPackageName(), new ResponseCallback() {
                @Override
                public void onResponse(boolean isConnected) {
                    appInfo.setmCerebrumSupported(isConnected);
                    if (isConnected)
                        setInfo();
                }
            });

        } catch (Exception e) {
            serviceCommunication = null;
            appInfo.setmCerebrumSupported(false);
        }
    }

    void stopService() {
        try {
            serviceCommunication.exit();

        } catch (Exception e) {
            serviceCommunication = null;
        }
    }

    private void doOp(int op) {

        if (!appInfo.isMCerebrumSupported()) return;
        switch (op) {
            case CONFIGURE:
                serviceCommunication.configure();
                break;
            case REPORT:
                serviceCommunication.report();
                break;
            case START_BACKGROUND:
                serviceCommunication.startBackground();
                break;
            case STOP_BACKGROUND:
                serviceCommunication.stopBackground();
                break;
            case INFO:
                Info info = serviceCommunication.getInfo();
                appInfo.setmCerebrum(info);
                if(!appInfo.isInitialized())
                    doOp(INIT);
                break;
            case INIT:
                serviceCommunication.initialize();
                appInfo.setInitialized(true);
                break;
            case CLEAR:
                serviceCommunication.clear();
                break;
        }
    }

    void configure() {
        doOp(CONFIGURE);
    }
    public void clear(){
        doOp(CLEAR);
    }

    public void report() {
        doOp(REPORT);
    }

    public void startBackground() {
        doOp(START_BACKGROUND);
    }

    public void stopBackground() {
        doOp(STOP_BACKGROUND);
    }

    void setInfo() {
        doOp(INFO);
    }

/*
    public void reset() {
        stopService(new ResponseCallback() {
            @Override
            public void onResponse(boolean isConnected) {
                initializeInfo();
                if (isInstalled())
                    startService(null);
            }
        });
    }
*/

}
