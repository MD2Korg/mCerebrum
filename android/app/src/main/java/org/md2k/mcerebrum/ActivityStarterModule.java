package org.md2k.mcerebrum;
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
import android.net.Uri;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.md2k.mcerebrum.phonesensor.ActivitySettings;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Expose Java to JavaScript.
 */
class ActivityStarterModule extends ReactContextBaseJavaModule {

    private static DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = null;

    ActivityStarterModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        eventEmitter = getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from JavaScript.
     */
    @Override
    public String getName() {
        return "ActivityStarter";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("MyEventName", "MyEventValue");
        return constants;
    }

    @ReactMethod
    void navigateToExample() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, ActivitySettings.class);
            activity.startActivity(intent);
        }
    }

    @ReactMethod
    void dialNumber(@Nonnull String number) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            activity.startActivity(intent);
        }
    }

    @ReactMethod
    void getActivityName(@Nonnull Callback callback) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            callback.invoke(activity.getClass().getSimpleName());
        }
    }

    @ReactMethod
    void getActivityNameAsPromise(@Nonnull Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            promise.resolve(activity.getClass().getSimpleName());
        }
    }

    @ReactMethod
    void callJavaScript() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            MyApplication application = (MyApplication) activity.getApplication();
            ReactNativeHost reactNativeHost = application.getReactNativeHost();
            ReactInstanceManager reactInstanceManager = reactNativeHost.getReactInstanceManager();
            ReactContext reactContext = reactInstanceManager.getCurrentReactContext();

            if (reactContext != null) {
                CatalystInstance catalystInstance = reactContext.getCatalystInstance();
                WritableNativeArray params = new WritableNativeArray();
                params.pushString("Hello, JavaScript!");
                catalystInstance.callFunction("JavaScriptVisibleToJava", "alert", params);
            }
        }
    }

    /**
     * To pass an object instead of a simple string, create a {@link WritableNativeMap} and populate it.
     */
    static void triggerAlert(@Nonnull String message) {
        eventEmitter.emit("MyEventValue", message);
    }
}