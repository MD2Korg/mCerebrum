/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
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

package org.md2k.mcerebrum.datakit.cerebralcortex;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.mcerebrum.cerebral_cortex.serverinfo.CCInfo;

import java.io.IOException;

/**
 * <code>CerebralCortex</code> background service.
 */
public class ServiceCerebralCortex extends Service {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = ServiceCerebralCortex.class.getSimpleName();

    /**
     * Instance of the <code>cerebralCortexManager</code> class.
     *
     * <p>
     *     Used to connect to <code>CerebralCortex</code>
     * </p>
     */
    private static CerebralCortexManager cerebralCortexManager;

    /**
     * Connects to <code>CerebralCortex</code>.
     */
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(br, new IntentFilter("SERVER_ERROR"));
        Log.d(TAG, "Connecting Cerebral Cortex");
        try {
            cerebralCortexManager = CerebralCortexManager.getInstance(ServiceCerebralCortex.this.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(CCInfo.getUrl()==null) stopSelf();
        else
            cerebralCortexManager.start();
    }

    /**
     * Unregisters the broadcast receiver and stops <code>cerebralCortexManager</code>.
     */
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
        if (cerebralCortexManager != null && cerebralCortexManager.isActive())
            cerebralCortexManager.stop();
        super.onDestroy();
    }

    /**
     * Not yet implemented.
     *
     * @param intent Android intent.
     * @return An IBinder object.
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        /**
         * Stops the service when <code>br</code> receives an <code>Intent</code>.
         *
         * @param context Android context
         * @param intent Android intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent <-- Is this something that needs to be implemented?
            stopSelf();
        }
    };

}
