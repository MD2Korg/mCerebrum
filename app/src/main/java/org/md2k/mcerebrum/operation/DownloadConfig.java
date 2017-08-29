package org.md2k.mcerebrum.operation;
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

public class DownloadConfig {
/*    Observable<Boolean> getObservableStart(final Activity activity){
        return Observable.just(User.getUser(activity))
                .flatMap(new Func1<User, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(User user) {
                        if(user==null)
                            return downloadConfig();
                        else return Observable.just(true);
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        if(User.getUser(activity)==null)
                            user = User.createFreebie(activity);
                        return true;
                    }
                });
    }
    private Observable<Boolean> downloadConfig(final Activity activity){
        return Observable.just(true)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
  /*                      dialog = new MaterialDialog.Builder(AbstractActivityBasics.this)
                                .content("Downloading configuration file...")
                                .progress(false, 100, true)
                                .show();
*/ /*                       return true;
                    }
                })
                .flatMap(new Func1<Boolean, Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(Boolean aBoolean) {
                        return new DownloadFile().download(Constants.CONFIG_DEFAULT_URL, Constants.getDirectoryTemp(AbstractActivityBasics.this),Constants.CONFIG_FILENAME_ZIP);
                    }
                }).map(new Func1<DownloadInfo, Boolean>() {
                    @Override
                    public Boolean call(DownloadInfo downloadInfo) {
                        if(downloadInfo.getProgress()==100) return true;
//                        dialog.setProgress((int) downloadInfo.getProgress());
                        return false;
                    }
                }).filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                }).map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return ZipUtils.unzipFile(Constants.getDirectoryTemp(activity)+"/"+Constants.CONFIG_FILENAME_ZIP, Constants.getDirectoryTemp(activity)+"/"+Constants.CONFIG_FILENAME_ZIP);
                    }
                }).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        dialog.dismiss();
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        Toasty.error(AbstractActivityBasics.this,"Error: Download failed (e="+throwable.getMessage()+")").show();
                        dialog.dismiss();
                    }
                });
    }
    */
}
