package org.md2k.mcerebrum.internet.download;
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

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DownloadFile {
    public Observable<DownloadInfo> download(String source, String destinationPath, String destinationFile) {
        Observable<DownloadInfo> observable;
        try {
            String[] parts = getParts(source);
            RetrofitInterface downloadService = createService(RetrofitInterface.class, parts[0]);
            observable = downloadService.downloadFileByUrlRx(parts[1])
                    .flatMap(processResponse(destinationPath, destinationFile));
        } catch (MalformedURLException e) {
            return Observable.error(e);
        }
        return observable;

    }
    private Func1<Response<ResponseBody>, Observable<DownloadInfo>> processResponse(final String destinationPath, final String destinationFile) {
        return new Func1<Response<ResponseBody>, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(final Response<ResponseBody> responseBodyResponse) {
                return Observable.create(new Observable.OnSubscribe<DownloadInfo>() {
                    @Override
                    public void call(Subscriber<? super DownloadInfo> subscriber) {
                        try {
                            downloadFile(responseBodyResponse.body(), subscriber, destinationPath, destinationFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }
    private void downloadFile(ResponseBody body, Subscriber<? super DownloadInfo> subscriber, String destinationPath, String destinationFile) throws IOException {
        int count;
        byte data[] = new byte[1024 * 4];
        long totalSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        boolean b = new File(destinationPath).mkdirs();
        File outputFile = new File(destinationPath, destinationFile);
        OutputStream output = new FileOutputStream(outputFile);
        long curSize = 0;
        while ((count = bis.read(data)) != -1) {
            curSize += count;
            DownloadInfo downloadInfo = new DownloadInfo(totalSize,curSize, false);
            output.write(data, 0, count);
            subscriber.onNext(downloadInfo);
        }
        DownloadInfo downloadInfo = new DownloadInfo(totalSize,curSize,true);
        output.flush();
        output.close();
        bis.close();
        subscriber.onNext(downloadInfo);
        subscriber.onCompleted();
    }

    private String[] getParts(String path) throws MalformedURLException {
        String parts[]=new String[2];
        URL aURL=new URL(path);
        parts[1]=aURL.getFile().substring(1);
        parts[0]=aURL.getProtocol()+"://"+aURL.getAuthority()+"/";
        return parts;
    }

    private <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();
        return retrofit.create(serviceClass);
    }
}
