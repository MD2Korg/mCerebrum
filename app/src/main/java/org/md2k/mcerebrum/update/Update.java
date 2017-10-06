package org.md2k.mcerebrum.update;
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

import android.content.Context;
import android.os.Environment;

import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.app.ApplicationManager;
import org.md2k.system.cerebralcortexwebapi.ServerManager;
import org.md2k.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.system.provider.ServerCP;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class Update {
    public static Observable<Boolean> hasUpdate(Context context,DataManager dataManager, ApplicationManager applicationManager) {
        return Observable.merge(hasUpdateConfigServer(context, dataManager.getDataCPManager().getServerCP()), hasUpdateApp(applicationManager))
                .filter(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                return aBoolean == true;
            }
        });

    }

    public static Observable<Boolean> hasUpdateApp(ApplicationManager applicationManager) {
        return applicationManager.hasUpdate();
    }

    public static Observable<Boolean> hasUpdateConfigServer(final Context context, final ServerCP serverCP) {
        serverCP.setLatestVersion(context, serverCP.getCurrentVersion());
            return Observable.just(true).map(new Func1<Boolean, Boolean>() {
                @Override
                public Boolean call(Boolean aBoolean) {
                    String latestVersion = ServerManager.getLastModified(serverCP.getServerAddress(), serverCP.getToken(), serverCP.getFileName());
                    serverCP.setLatestVersion(context, latestVersion);
                    if(serverCP.getCurrentVersion().equals(latestVersion)) return false;
                    else return true;
                }
            });
    }

    public static Observable<Boolean> updateConfigServer(final DataManager dataManager, final ServerCP serverCP, final String dir) {
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                if (!ServerManager.download(serverCP.getServerAddress(), serverCP.getToken(), serverCP.getFileName()))
                    return Observable.error(new Throwable("Download failed"));
                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        String a = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        if (!unzipFile(a + "/config.zip", dir))
                            return Observable.error(new Throwable("Failed to unzip"));
                        else {
                            if (!dataManager.getDataFileManager().read()) {
                                dataManager.loadDefault();
                                return Observable.error(new Throwable("Configuration file format error"));
                            } else {
                                dataManager.updateDataDP();
                                MinioObjectStats minioObject=ServerManager.getConfigFile(serverCP.getServerAddress(), serverCP.getToken(), serverCP.getFileName());
                                serverCP.setCurrentVersion(MyApplication.getContext(), minioObject.getLastModified());
                                serverCP.setLatestVersion(MyApplication.getContext(), minioObject.getLastModified());
                                return Observable.just(true);
                            }
                        }
                    }

                });
    }
}
