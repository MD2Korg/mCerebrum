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

import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;
import org.md2k.md2k.system.app.InstallInfo;

import rx.Observable;
import rx.functions.Func1;

public class AppFromGithub {
    Observable<ReleaseInfo> getLatestVersion(String downloadLink) {
        String[] parts = downloadLink.split("/");
        if (parts.length != 2) return Observable.error(new Throwable("Illegal download link"));
        Github github = new Github();
        return github.getReleaseLatest(parts[0], parts[1]);
    }

    Observable<ReleaseInfo> getExpectedVersion(String downloadLink, final String expectedVersion) {
        String[] parts = downloadLink.split("/");
        if (parts.length != 2) return Observable.error(new Throwable("Illegal download link"));
        Github github = new Github();
        return github.getReleases(parts[0], parts[1]).map(new Func1<ReleaseInfo[], ReleaseInfo>() {
            @Override
            public ReleaseInfo call(ReleaseInfo[] releaseInfos) {
                for (ReleaseInfo releaseInfo : releaseInfos)
                    if (releaseInfo.getName().equalsIgnoreCase(expectedVersion)) return releaseInfo;
                return null;
            }
        });
    }

    Observable<ReleaseInfo[]> getAllVersions(String downloadLink) {
        String[] parts = downloadLink.split("/");
        if (parts.length != 2)
            return Observable.error(new Throwable("Invalid download link in configuration file"));
        Github github = new Github();
        return github.getReleases(parts[0], parts[1]);
    }

    Observable<VersionInfo> getVersion(String downloadLink, String expectedVersion) {
        Observable<ReleaseInfo> releaseInfoObservable;
        if (expectedVersion == null)
            releaseInfoObservable = getLatestVersion(downloadLink);
        else releaseInfoObservable = getExpectedVersion(downloadLink, expectedVersion);
        return releaseInfoObservable
                .map(new Func1<ReleaseInfo, VersionInfo>() {
                    @Override
                    public VersionInfo call(ReleaseInfo releaseInfo) {
                        VersionInfo versionInfo = new VersionInfo();
                        for (int i = 0; i < releaseInfo.getAssets().length; i++) {
                            if (releaseInfo.getAssets()[i].getBrowser_download_url().endsWith(".apk"))
                                versionInfo.downloadURL = releaseInfo.getAssets()[i].getBrowser_download_url();
                        }
                        versionInfo.versionName = releaseInfo.getName();
                        versionInfo.created_at = releaseInfo.getCreated_at();
                        versionInfo.published_at = releaseInfo.getPublished_at();
                        return versionInfo;
                    }
                });
    }


    Observable<DownloadInfo> downloadLatest(String downloadLink, final String filePath,
                                            final String fileName) {
        return getLatestVersion(downloadLink).map(new Func1<ReleaseInfo, String>() {
            @Override
            public String call(ReleaseInfo releaseInfo) {
                for (int i = 0; i < releaseInfo.getAssets().length; i++) {
                    if (releaseInfo.getAssets()[i].getBrowser_download_url().endsWith(".apk"))
                        return releaseInfo.getAssets()[i].getBrowser_download_url();
                }
                return null;
            }
        }).flatMap(new Func1<String, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(String s) {
                if (s == null) return null;
                return new DownloadFile().download(s, filePath, fileName);
            }
        });
    }

    Observable<DownloadInfo> downloadExpected(String downloadLink, String expectedVersion,
                                              final String filePath, final String fileName) {
        return getExpectedVersion(downloadLink, expectedVersion).map(new Func1<ReleaseInfo, String>() {
            @Override
            public String call(ReleaseInfo releaseInfo) {
                if (releaseInfo == null) return null;
                for (int i = 0; i < releaseInfo.getAssets().length; i++) {
                    if (releaseInfo.getAssets()[i].getBrowser_download_url().endsWith(".apk"))
                        return releaseInfo.getAssets()[i].getBrowser_download_url();
                }
                return null;
            }
        }).flatMap(new Func1<String, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(String s) {
                if (s == null)
                    return Observable.error(new Throwable("Expected file not found"));
                return new DownloadFile().download(s, filePath, fileName);
            }
        });
    }

}
