package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;

import org.md2k.mcerebrum.system.update.Update;

import rx.Observer;
import rx.Subscription;

public class ActivityMain extends AbstractActivityMenu {
    Subscription subscriptionCheckUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionCheckUpdate=Update.checkUpdate(this)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

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
}

