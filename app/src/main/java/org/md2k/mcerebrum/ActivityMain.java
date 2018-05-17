package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;

import org.md2k.mcerebrum.system.update.Update;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ActivityMain extends AbstractActivityMenu {
    Subscription subscriptionCheckUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionCheckUpdate= Observable.just(true).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                return Update.checkUpdate(ActivityMain.this);
            }
        }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        updateUI();
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateUI();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Handle Code
    }
    public void onDestroy(){
        if (subscriptionCheckUpdate != null && !subscriptionCheckUpdate.isUnsubscribed())
            subscriptionCheckUpdate .unsubscribe();
        super.onDestroy();
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }
}

