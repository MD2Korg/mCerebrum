package org.md2k.mcerebrum.system.appinfo;
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

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import rx.Observable;
import rx.Subscriber;

public class AppFromJson {
    public Observable<VersionInfo> getVersion(final Context context, final String downloadLink) {
        return Observable.create(new Observable.OnSubscribe<VersionInfo>() {
            @Override
            public void call(final Subscriber<? super VersionInfo> subscriber) {
                try {
                    AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(context)
                            .setUpdateFrom(UpdateFrom.JSON)
                            .setUpdateJSON(downloadLink)
                            //.setUpdateFrom(UpdateFrom.AMAZON)
                            //.setUpdateFrom(UpdateFrom.GITHUB)
                            //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                            //...
                            .withListener(new AppUpdaterUtils.UpdateListener() {
                                @Override
                                public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                    VersionInfo versionInfo=new VersionInfo();
                                    versionInfo.versionName=update.getLatestVersion();
                                    versionInfo.versionCode= update.getLatestVersionCode();
                                    versionInfo.downloadURL=update.getUrlToDownload().toString();
                                    versionInfo.releaseInfo=update.getReleaseNotes();
                                    subscriber.onNext(versionInfo);    // Pass on the data to subscriber
                                    subscriber.onCompleted();     // Signal about the completion subscriber
                                }

                                @Override
                                public void onFailed(AppUpdaterError error) {
                                    subscriber.onNext(null);    // Pass on the data to subscriber
                                    subscriber.onCompleted();     // Signal about the completion subscriber
                                }
                            });
                    appUpdaterUtils.start();
                } catch (Exception e) {
                    subscriber.onError(e);        // Signal about the error to subscriber
                }
            }
        });
    }

}
